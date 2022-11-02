package com.example.wapapp2.repository

import android.app.Application
import android.content.Context

class CalcRoomRepository private constructor(context: Context) {
    companion object {
        private lateinit var INSTANCE: CalcRoomRepository

        fun getINSTANCE() = INSTANCE

        fun initialize(context: Context) {
            INSTANCE = CalcRoomRepository(context)
        }
    }
}