package com.example.wapapp2.repository

import com.example.wapapp2.repository.interfaces.FcmRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.RemoteMessageCreator

class FcnRepositoryImpl : FcmRepository {
    override suspend fun sendMessage(map: MutableMap<String, String>) {
        val fcm = FirebaseMessaging.getInstance()
        val message = RemoteMessage()
        message.data.putAll(map)
    }
}