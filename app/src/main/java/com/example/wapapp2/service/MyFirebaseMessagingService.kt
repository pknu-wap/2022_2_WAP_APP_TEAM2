package com.example.wapapp2.service

import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.notification.helper.ChatNotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /** title = ChatRoomName  **/
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val chatDTO : ChatDTO?
        val room = data["room_name"]?.let{
            chatDTO = ChatDTO(
                data["chatDTO_username"]!!,
                Date(),
                data["chatDTO_sendedTime"]!!,
                data["chatDTO_senderId"]!!
            )
        }
    }

    /** 토큰 생성시 서버에 등록 과정 필요 **/
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //토큰 등록

        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(it.uid)
                .collection("FCM_TOKEN")
                .document().set(token)
        }
    }


    private fun sendNotification(title : String ,chatDTO: ChatDTO) {
        val notificationBuilder = ChatNotificationHelper.getINSTANCE(this)
            .notifyNotification(this, title , chatDTO)
    }
}