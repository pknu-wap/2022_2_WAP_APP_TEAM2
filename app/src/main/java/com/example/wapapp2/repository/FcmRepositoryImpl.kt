package com.example.wapapp2.repository

import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.PushDeviceGroupDTO
import com.example.wapapp2.model.notifications.TokenPushNotificationDTO
import com.example.wapapp2.model.notifications.TopicPushNotificationDTO
import com.example.wapapp2.repository.interfaces.FcmRepository
import com.example.wapapp2.retrofit.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject

object FcmRepositoryImpl : FcmRepository {
    override suspend fun sendFcmToTopic(notificationType: NotificationType, topic: String, data: Any) {
        val body = makeBody(notificationType, data)

        val notificationDTO =
                TopicPushNotificationDTO("/topics/${topic}", TopicPushNotificationDTO.Notification("", body.toString()))
        val response = RetrofitClient.getClients().sendFcmToTopic(notificationDTO)
    }

    override suspend fun sendFcmToRecipients(notificationType: NotificationType, tokens: MutableList<String>, data: Any) {
        val body = makeBody(notificationType, data)

        for (token in tokens) {
            val notificationDTO =
                    TokenPushNotificationDTO(TokenPushNotificationDTO.Notification("", body.toString()), token)
            val response = RetrofitClient.getClients().sendFcmToRecipient(notificationDTO)
            if (response.isSuccessful) {

            }
        }
    }

    override fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    override fun unSubscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }

    private fun makeBody(notificationType: NotificationType, data: Any): JsonObject {
        val body = JsonObject()
        // 공통 필드
        body.addProperty("type", notificationType.name)
        body.add("data", Gson().toJsonTree(data))

        return body
    }
}