package com.example.wapapp2.main

import android.app.Application
import com.example.wapapp2.repository.CalcRoomRepository
import com.example.wapapp2.repository.FriendsRepository

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FriendsRepository.initialize()
        CalcRoomRepository.initialize(applicationContext)
    }

}