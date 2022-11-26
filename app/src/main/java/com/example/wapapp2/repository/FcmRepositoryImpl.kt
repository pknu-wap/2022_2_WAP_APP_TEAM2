package com.example.wapapp2.repository

import com.example.wapapp2.model.notifications.MultipleRecipientsPushNotificationDTO
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.TopicPushNotificationDTO
import com.example.wapapp2.model.notifications.send.SendFcmCalcRoomDTO
import com.example.wapapp2.repository.interfaces.FcmRepository
import com.example.wapapp2.retrofit.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject

object FcmRepositoryImpl : FcmRepository {
    override suspend fun sendFcmToTopic(notificationType: NotificationType, topic: String, data: Any) {
        val body = makeBody(notificationType, topic, data)

        val notificationDTO =
                TopicPushNotificationDTO("/topics/${topic}", TopicPushNotificationDTO.Notification("", body.toString()))
        val response = RetrofitClient.getClients().sendFcmToTopic(notificationDTO)
    }

    override suspend fun sendFcmToMultipleDevices(notificationType: NotificationType, tokens: MutableList<String>, data: Any) {
        val body = makeBody(notificationType, "", data)

        val notificationDTO = MultipleRecipientsPushNotificationDTO(MultipleRecipientsPushNotificationDTO.Notification(
                "", body.toString()), tokens)
        val response = RetrofitClient.getClients().sendFcmToMultipleRecipients(notificationDTO)
    }

    override fun subscribeToCalcRoom(roomId: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(roomId).addOnCompleteListener {
            if (it.isSuccessful) {

            }
        }
    }

    override fun unSubscribeToCalcRoom(roomId: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(roomId)
    }

    private fun makeBody(notificationType: NotificationType, calcRoomId: String, data: Any): JsonObject {
        val body = JsonObject()
        // 공통 필드
        body.addProperty("type", notificationType.name)
        body.addProperty("calcRoomId", calcRoomId)
        body.add("data", Gson().toJsonTree(data))

        return body
    }
}