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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MyBankAccountsViewModel : ViewModel() {
    val bankList = ArrayList<BankDTO>()
    var defaultBankAccountHolder: String? = null
    var selectedBank: BankDTO? = null
    val addedMyBankAccount = MutableLiveData<Boolean>()
    val removedMyBankAccount = MutableLiveData<Boolean>()
    val myBankAccounts = MutableLiveData<MutableList<BankAccountDTO>>()

    private val repository = MyBankAccountRepositoryImpl.getINSTANCE()

    fun onSelectedBank(bankDTO: BankDTO) {
        selectedBank = bankDTO
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

    fun getMyBankAccounts() {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                repository.getMyBankAccounts()
            }
            result.await()
            withContext(Main) {
                myBankAccounts.value = result.await()
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
}