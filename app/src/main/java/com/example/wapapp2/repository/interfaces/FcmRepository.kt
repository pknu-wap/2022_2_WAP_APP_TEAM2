package com.example.wapapp2.repository.interfaces

interface FcmRepository {
    suspend fun sendMessage(map: MutableMap<String, String>)
}