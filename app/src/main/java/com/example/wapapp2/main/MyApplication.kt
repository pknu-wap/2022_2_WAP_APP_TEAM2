package com.example.wapapp2.main

import android.app.Application
import com.google.firebase.FirebaseApp
import net.danlew.android.joda.JodaTimeAndroid

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        JodaTimeAndroid.init(applicationContext)
    }

}