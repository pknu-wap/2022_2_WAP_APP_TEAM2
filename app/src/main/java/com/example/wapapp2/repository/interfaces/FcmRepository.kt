package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.notifications.PushNotificationDTO

interface FcmRepository {
    suspend fun sendMessage(notificaiton: PushNotificationDTO)
    fun subscribeToCalcRoomChat(roomId: String)
    fun unSubscribeToCalcRoomChat(roomId: String)
}