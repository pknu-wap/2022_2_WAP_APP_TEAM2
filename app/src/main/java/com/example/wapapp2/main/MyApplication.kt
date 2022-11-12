package com.example.wapapp2.main

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore

import com.google.firebase.FirebaseApp

import net.danlew.android.joda.JodaTimeAndroid
import java.util.prefs.Preferences

class MyApplication : Application() {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "currentUser")


    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(applicationContext)

        JodaTimeAndroid.init(applicationContext)
    }

}