package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.collection.arrayMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.main.MyApplication.Companion.BANK_MAPS
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.BankDTO
import com.example.wapapp2.repository.MyBankAccountRepositoryImpl
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class MyBankAccountsViewModel : ViewModel() {
    private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val repository = MyBankAccountRepositoryImpl.getINSTANCE()

    var defaultBankAccountHolder: String? = null
    var currentSelectedBank: BankDTO? = null
    val myBankAccounts = arrayMapOf<String, BankAccountDTO>()

    fun addMyBankAccount(bankAccountDTO: BankAccountDTO) {
        CoroutineScope(Dispatchers.Default).launch {

            val result = async {
                repository.addMyBankAccount(bankAccountDTO)
            }
            result.await()

        }
    }

    fun isDuplicateBankAccount(bankAccountDTO: BankAccountDTO): Boolean {
        return if (myBankAccounts.containsKey(bankAccountDTO.accountNumber)) {
            //계좌번호 일치 여부 확인 -> 은행 일치 확인 -> 일치하면 추가안함
            myBankAccounts[bankAccountDTO.accountNumber]!!.bankId == bankAccountDTO.bankId
        } else
            false
    }

    fun modifyMyBankAccount(modified: BankAccountDTO, original: BankAccountDTO) {
        CoroutineScope(Dispatchers.Default).launch {
            val map = mutableMapOf<String, Any?>()
            if (original.bankId != modified.bankId)
                map["bankId"] = modified.bankId
            if (original.accountNumber != modified.accountNumber)
                map["accountNumber"] = modified.accountNumber
            if (original.accountHolder != modified.accountHolder)
                map["accountHolder"] = modified.accountHolder

            val result = async {
                repository.modifyMyBankAccount(original.id, map)
            }
            result.await()
        }
    }

    fun removeMyBankAccount(bankAccountDTO: BankAccountDTO) {
        CoroutineScope(Dispatchers.Default).launch {
            if (myBankAccounts.containsKey(bankAccountDTO.accountNumber)) {
                //계좌번호 일치 여부 확인 -> 은행 일치 확인 -> 일치하면 삭제
                if (myBankAccounts[bankAccountDTO.accountNumber]!!.bankId == bankAccountDTO.bankId)
                    myBankAccounts.remove(bankAccountDTO.accountNumber)
            }

            val result = async {
                repository.removeMyBankAccount(bankAccountDTO.id)
            }
            result.await()
        }
    }

    fun getMyBankAccountsOptions(): FirestoreRecyclerOptions<BankAccountDTO> {
        val query = fireStore.collection(FireStoreNames.users.name)
                .document(auth.currentUser?.uid!!)
                .collection(FireStoreNames.bankAccounts.name).orderBy("bankId", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<BankAccountDTO>()
                .setQuery(query, MetadataChanges.INCLUDE) {
                    val dto = it.toObject<BankAccountDTO>()!!
                    dto.id = it.id
                    dto.bankDTO = BANK_MAPS[dto.bankId]
                    myBankAccounts[dto.accountNumber] = dto
                    dto
                }
                .build()

        return options
    }
}