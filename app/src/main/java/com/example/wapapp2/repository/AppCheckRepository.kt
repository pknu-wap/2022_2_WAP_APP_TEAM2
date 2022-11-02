package com.example.wapapp2.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.R


class AppCheckRepository private constructor(){
    val BankPakageLiveData: MutableLiveData<ArrayList<ApplicationInfo>> = MutableLiveData<ArrayList<ApplicationInfo>>()

    companion object {
        private lateinit var INSTANCE: AppCheckRepository

        fun initialize() {
            INSTANCE = AppCheckRepository()
        }

        fun getINSTANCE() = INSTANCE
    }

    /** bank packageList livedata로 저장 **/
    fun setBankAppInfoList(context : Context) {
        val bank_infos = ArrayList<ApplicationInfo>()

        val packageManager: PackageManager = context.packageManager
        val packages: List<ApplicationInfo> = packageManager.getInstalledApplications(0)
        val setBanks = context.resources.getStringArray(R.array.bank_package_list).toHashSet()

        for (pac : ApplicationInfo in packages){
            if (setBanks.contains(pac.packageName)){
                bank_infos.add(pac)
            }
        }

        BankPakageLiveData.value = bank_infos
    }


    fun getBankAppIcon(context: Context, applicationInfo: ApplicationInfo) : Drawable{
        return context.packageManager.getApplicationIcon(applicationInfo)
    }


    fun getAppName(context: Context, applicationInfo: ApplicationInfo) : String{
        val App_name: String = context.packageManager.getApplicationLabel(applicationInfo) as String
        return App_name
    }
}