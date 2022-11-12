package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.R
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.BankDTO
import com.example.wapapp2.repository.MyBankAccountRepositoryImpl
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MyBankAccountsViewModel(application: Application) : AndroidViewModel(application) {
    private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val repository = MyBankAccountRepositoryImpl.getINSTANCE()

    val bankList = mutableListOf<BankDTO>()
    val bankMaps = mutableMapOf<String, BankDTO>()

    var defaultBankAccountHolder: String? = null
    var selectedBank: BankDTO? = null


    val addedMyBankAccount = MutableLiveData<Boolean>()
    val removedMyBankAccount = MutableLiveData<Boolean>()

    init {
        val bankNamesList = application.resources.getStringArray(R.array.bank_name_list)
        val bankIconsList = application.resources.getIntArray(R.array.bank_icon_list)
        var bankDTO: BankDTO? = null

        for ((index, value) in bankNamesList.withIndex()) {
            bankDTO = BankDTO(value, bankIconsList[index], index.toString())
            bankList.add(bankDTO)
            bankMaps[index.toString()] = bankDTO
        }
    }

    fun addMyBankAccount(bankAccountDTO: BankAccountDTO) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                repository.addMyBankAccount(bankAccountDTO)
            }
            result.await()
            withContext(Main) {
                addedMyBankAccount.value = result.await()
            }
        }
    }

    fun modifyMyBankAccount(bankAccountDTO: BankAccountDTO) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                repository.addMyBankAccount(bankAccountDTO)
            }
            result.await()
            withContext(Main) {
                addedMyBankAccount.value = result.await()
            }
        }
    }

    fun removeMyBankAccount(id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                repository.removeMyBankAccount(id)
            }
            result.await()
            withContext(Main) {
                removedMyBankAccount.value = result.await()
            }
        }
    }

    fun getMyBankAccountsOptions(): FirestoreRecyclerOptions<BankAccountDTO> {
        val query = fireStore.collection(FireStoreNames.users.name)
                .document(auth.currentUser?.uid!!)
                .collection(FireStoreNames.bankAccounts.name).orderBy("bankId", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<BankAccountDTO>()
                .setQuery(query) {
                    val dto = it.toObject<BankAccountDTO>()!!
                    dto.id = it.id
                    dto.bankDTO = bankMaps[dto.bankId]
                    dto
                }
                .build()

        return options
    }
}