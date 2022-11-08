package com.example.wapapp2.main

import android.app.Application
import com.example.wapapp2.repository.AppCheckRepository
import com.example.wapapp2.repository.FriendsRepository
import com.example.wapapp2.repository.ReceiptRepository
import net.danlew.android.joda.JodaTimeAndroid

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCheckRepository.initialize()
        FriendsRepository.initialize()
        ReceiptRepository.initialize()
        JodaTimeAndroid.init(applicationContext)
    }

}