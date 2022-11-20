package com.example.wapapp2.viewmodel.fcm

import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.PushNotificationDTO
import com.example.wapapp2.repository.FcmRepositoryImpl
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FcmViewModel : ViewModel() {

    fun sendMessage(chatDTO: ChatDTO, roomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val body = JsonObject()
            body.addProperty("senderId", chatDTO.senderId)
            body.addProperty("userName", chatDTO.userName)
            body.addProperty("msg", chatDTO.msg)
            body.addProperty("type", NotificationType.Chat.name)

            val notificationDTO = PushNotificationDTO("/topics/$roomId", PushNotificationDTO.Notification(
                    chatDTO.userName, body.toString()
            ))
            FcmRepositoryImpl.sendMessage(notificationDTO)
        }
    }

    fun subscribeToCalcRoomChat(roomId: String) {
        FcmRepositoryImpl.subscribeToCalcRoomChat(roomId)
    }

    fun unSubscribeToCalcRoomChat(roomId: String) {
        FcmRepositoryImpl.unSubscribeToCalcRoomChat(roomId)
    }
}