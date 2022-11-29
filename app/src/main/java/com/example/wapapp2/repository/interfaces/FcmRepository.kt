package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.notifications.NotificationType

interface FcmRepository {
    suspend fun sendFcmToTopic(notificationType: NotificationType, topic: String, data: Any)
    suspend fun sendFcmToRecipients(notificationType: NotificationType, tokens: MutableList<String>, data: Any)

    fun subscribeToTopic(topic: String)
    fun unSubscribeToTopic(topic: String)
}