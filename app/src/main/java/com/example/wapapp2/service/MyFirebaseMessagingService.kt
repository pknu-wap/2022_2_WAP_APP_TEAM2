package com.example.wapapp2.service

import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.notification.helper.ChatNotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /** title = ChatRoomName  **/
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.data["room_name"]
        val chatDTO = message.data["chatDTO"] as ChatDTO
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }

    /** 토큰 생성시 서버에 등록 과정 필요 **/
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //토큰 등록
    }


    private fun sendNotification(title : String ,chatDTO: ChatDTO) {
        val notificationBuilder = ChatNotificationHelper.getINSTANCE(this)
            .notifyNotification(this, title , chatDTO)
    }
}