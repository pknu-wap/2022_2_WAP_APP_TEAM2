package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.notifications.MultipleRecipientsPushNotificationDTO
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.TopicPushNotificationDTO
import com.google.gson.JsonObject

interface FcmRepository {
    suspend fun sendFcmToTopic(notificationType: NotificationType, topic: String, data: Any)
    suspend fun sendFcmToMultipleDevices(notificationType: NotificationType, tokens: MutableList<String>, data: Any)

    fun subscribeToCalcRoom(roomId: String)
    fun unSubscribeToCalcRoom(roomId: String)
}