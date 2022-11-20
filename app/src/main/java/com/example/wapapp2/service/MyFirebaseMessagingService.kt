package com.example.wapapp2.service

import android.util.Log
import com.example.wapapp2.datastore.MyDataStore
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.datastore.FcmTokenDTO
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.ReceivedPushNotificationDTO
import com.example.wapapp2.notification.helper.ChatNotificationHelper
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /** title = ChatRoomName  **/
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val body = message.notification?.body.toString()

        if (!body.isNullOrEmpty()) {
            //영수증, 채팅, 정산 구분
            val receivedDTO = Gson().fromJson(body, ReceivedPushNotificationDTO::class.java)


        }
    }

    /** 토큰 생성시 서버에 등록 과정 필요 **/
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            MyDataStore.getINSTANCE().updateFcmToken(FcmTokenDTO(token))
        }
    }

    private fun chat(data: Map<String, String>) {
        val chatNotificationHelper = ChatNotificationHelper.getINSTANCE(applicationContext)

        val senderId = data["senderId"]!!
        val senderName: String? = if (FriendsViewModel.myFriendMap.containsKey(senderId))
            FriendsViewModel.myFriendMap[senderId]!!.alias
        else
            data["senderName"]

        val chatDTO = ChatDTO(senderName!!, null, data["msg"]!!, senderId)
        chatNotificationHelper.notifyNotification(applicationContext, data["roomName"]!!, chatDTO)
    }

    private fun receipt(data: Map<String, String>) {
    }

    private fun calculation(data: Map<String, String>) {
    }
}