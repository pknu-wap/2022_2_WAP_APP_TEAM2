package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.wapapp2.R
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.BankDTO

class MyBankAccountsViewModel(application: Application) : AndroidViewModel(application) {
    val bankList = ArrayList<BankDTO>()
    var defaultBankAccountHolder: String? = null
    var selectedBank: BankDTO? = null

    init {
        val bankNamesList = application.resources.getStringArray(R.array.bank_name_list)
        val bankIconsList = application.resources.getIntArray(R.array.bank_icon_list)

        for ((index, value) in bankNamesList.withIndex()) {
            bankList.add(BankDTO(value, bankIconsList[index], index.toString()))
        }
        defaultBankAccountHolder = "박준성"
    }


    fun onSelectedBank(bankDTO: BankDTO) {
        selectedBank = bankDTO
    }

    fun addNewMyBankAccount(bankAccountDTO: BankAccountDTO) {

    }

    fun removeMyBankAccount(bankAccountDTO: BankAccountDTO) {

    }
}