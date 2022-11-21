package com.example.wapapp2.viewmodel.fcm

import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.PushNotificationDTO
import com.example.wapapp2.model.notifications.send.SendFcmChatDTO
import com.example.wapapp2.model.notifications.send.SendFcmReceiptDTO
import com.example.wapapp2.repository.FcmRepositoryImpl
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FcmViewModel : ViewModel() {

    fun sendChat(chatDTO: ChatDTO, calcRoomDTO: CalcRoomDTO) {
        CoroutineScope(Dispatchers.IO).launch {
            val body = JsonObject()
            body.addProperty("senderId", chatDTO.senderId)
            body.addProperty("userName", chatDTO.userName)
            body.addProperty("msg", chatDTO.msg)
            body.addProperty("type", NotificationType.Chat.name)

            val sendFcmChatDTO = SendFcmChatDTO(chatDTO, calcRoomDTO.name, calcRoomDTO.id!!)

            val data = Gson().toJsonTree(sendFcmChatDTO)
            body.add("data", data)

            val notificationDTO = PushNotificationDTO("/topics/${calcRoomDTO.id!!}", PushNotificationDTO.Notification(
                    chatDTO.userName, body.toString()
            ))
            FcmRepositoryImpl.sendMessage(notificationDTO)
        }
    }

    fun sendCalculation(receiptDTO: ReceiptDTO, calcRoomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val body = JsonObject()
            body.addProperty("type", NotificationType.Calculation.name)

            val sendFcmReceiptDTO = SendFcmReceiptDTO(createdTime = receiptDTO.date.toString(), payersId = receiptDTO.payersId, name =
            receiptDTO.name, totalMoney = receiptDTO.totalMoney, imgUrl = receiptDTO.imgUrl, roomId = calcRoomId, receiptImgBitmap = null)

            val data = Gson().toJsonTree(sendFcmReceiptDTO)
            body.add("data", data)

            val notificationDTO = PushNotificationDTO("/topics/${calcRoomId}", PushNotificationDTO.Notification(
                    sendFcmReceiptDTO.name, body.toString()))
            FcmRepositoryImpl.sendCalculation(notificationDTO)
        }
    }

    fun subscribeToCalcRoomChat(roomId: String) {
        FcmRepositoryImpl.subscribeToCalcRoomChat(roomId)
    }

    fun unSubscribeToCalcRoomChat(roomId: String) {
        FcmRepositoryImpl.unSubscribeToCalcRoomChat(roomId)
    }
}