package com.example.wapapp2.main

import android.app.Application
import android.content.res.TypedArray
import androidx.collection.arrayMapOf
import com.example.wapapp2.R
import com.example.wapapp2.datastore.MyDataStore
import com.example.wapapp2.model.BankDTO
import com.example.wapapp2.repository.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import net.danlew.android.joda.JodaTimeAndroid


class MyApplication : Application() {
    companion object {
        val BANK_MAPS = arrayMapOf<String, BankDTO>()
    }

    override fun onCreate() {
        super.onCreate()
        MyDataStore.initialize(applicationContext)
        FirebaseApp.initializeApp(applicationContext)

        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
        JodaTimeAndroid.init(applicationContext)
        initBanks()
        FriendsLocalRepositoryImpl.initialize(applicationContext)

        CalendarRepositoryImpl.initialize()
        ReceiptImgRepositoryImpl.initialize()
        ReceiptRepositoryImpl.initialize()
        AppCheckRepository.initialize()
        FriendsRepositoryImpl.initialize()
        ChatRepositorylmpl.initialize()
        UserRepositoryImpl.initialize()
        MyBankAccountRepositoryImpl.initialize()
        CalcRoomRepositorylmpl.initialize()
    }

    private fun initBanks() {
        val bankNamesList = resources.getStringArray(R.array.bank_name_list)
        val bankIconsList: TypedArray = resources.obtainTypedArray(R.array.bank_icon_list)

        for ((index, value) in bankNamesList.withIndex()) {
            BANK_MAPS[index.toString()] = BankDTO(value, bankIconsList.getResourceId(index, 0), index.toString())
        }
    }

}