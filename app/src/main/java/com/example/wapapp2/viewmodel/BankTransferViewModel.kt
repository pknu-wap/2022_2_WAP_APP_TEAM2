package com.example.wapapp2.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.BankAppDTO
import com.example.wapapp2.repository.AppCheckRepository

class BankTransferViewModel(application: Application) : AndroidViewModel(application) {
    private val bank_repo = AppCheckRepository.getINSTANCE()
    private val _installedBankApps: MutableLiveData<ArrayList<BankAppDTO>> = MutableLiveData<ArrayList<BankAppDTO>>()
    val installedBankApps get() = _installedBankApps

    var selectedBankAccount: BankAccountDTO? = null

    fun setBankAppInfoList(context: Context) {
        bank_repo.setBankAppInfoList(context)
    }

    fun loadInstalledBankApps(context: Context) {
        val appInfos = bank_repo.setBankAppInfoList(context)
        val apps = ArrayList<BankAppDTO>()

        for (appInfo in appInfos) {
            apps.add(BankAppDTO(bank_repo.getAppName(context, appInfo), bank_repo.getBankAppIcon(context, appInfo), appInfo.packageName))
        }
        _installedBankApps.value = apps
    }
}