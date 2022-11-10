package com.example.wapapp2.repository

import android.app.Application
import android.content.Context

class CalcRoomRepository private constructor() {
    companion object {
        private lateinit var INSTANCE: CalcRoomRepository

        fun getINSTANCE() = INSTANCE

        fun initialize() {
            INSTANCE = CalcRoomRepository()
        }
    }
}