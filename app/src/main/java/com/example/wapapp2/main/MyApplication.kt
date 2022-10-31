package com.example.wapapp2.main

import android.app.Application
import com.example.wapapp2.repository.FriendRepository

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FriendRepository.initialize()

    }

}