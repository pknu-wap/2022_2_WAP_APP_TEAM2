package com.example.wapapp2.repository

class ChatRepository private constructor(){
    companion object {
        private lateinit var INSTANCE: ChatRepository

        fun getINSTANCE() = INSTANCE

        fun initialize() {
            INSTANCE = ChatRepository()

        }
    }
}