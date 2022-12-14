package com.example.wapapp2.service

import com.example.wapapp2.datastore.MyDataStore
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.ReceivedPushNotificationDTO
import com.example.wapapp2.model.notifications.send.SendFcmCalcRoomDTO
import com.example.wapapp2.model.notifications.send.SendFcmCalcRushDTO
import com.example.wapapp2.model.notifications.send.SendFcmChatDTO
import com.example.wapapp2.model.notifications.send.SendFcmReceiptDTO
import com.example.wapapp2.notification.helper.CalcRushNotificationHelper
import com.example.wapapp2.notification.helper.ChatNotificationHelper
import com.example.wapapp2.notification.helper.NewCalcRoomNotificationHelper
import com.example.wapapp2.notification.helper.ReceiptNotificationHelper
import com.example.wapapp2.repository.FcmRepositoryImpl
import com.example.wapapp2.repository.FriendsLocalRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /** title = ChatRoomName  **/
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val body = message.notification?.body

        if (!body.isNullOrEmpty()) {
            //영수증, 채팅, 정산 구분
            val receivedDTO = Gson().fromJson(body, ReceivedPushNotificationDTO::class.java)

            when (receivedDTO.type) {
                NotificationType.Chat -> chat(receivedDTO)
                NotificationType.Receipt -> receipt(receivedDTO)
                NotificationType.NewCalcRoom -> newCalcRoom(receivedDTO)
                NotificationType.CalcRush -> calcRush(receivedDTO)
                else -> {}
            }
        }
    }

    /** 토큰 변경 시 서버에 토큰 변경 등록 **/
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            MyDataStore.getINSTANCE().updateFcmToken(token)
        }
    }

    /**
     * 새로운 채팅 알림
     */
    private fun chat(receivedPushNotificationDTO: ReceivedPushNotificationDTO) {
        CoroutineScope(Dispatchers.IO).launch {
            val sendFcmChatDTO = Gson().fromJson(receivedPushNotificationDTO.data, SendFcmChatDTO::class.java)
            val senderId = sendFcmChatDTO.chatDTO.senderId
            FcmRepositoryImpl.subscribeToTopic(sendFcmChatDTO.roomId)

            val friendsLocalRepository = FriendsLocalRepositoryImpl.getINSTANCE()
            friendsLocalRepository.get(senderId).collect { value ->
                val senderName: String = //내 친구 목록에 있음
                        value?.alias ?: //내 친구 목록에 없음
                        sendFcmChatDTO.chatDTO.userName
                sendFcmChatDTO.chatDTO.userName = senderName

                withContext(Main) {
                    val chatNotificationHelper = ChatNotificationHelper.getINSTANCE(applicationContext)
                    chatNotificationHelper.notifyNotification(applicationContext, sendFcmChatDTO)
                }
            }
        }

    }

    /**
     * 영수증 추가 알림
     */
    private fun receipt(receivedPushNotificationDTO: ReceivedPushNotificationDTO) {
        val notificationHelper = ReceiptNotificationHelper.getINSTANCE(applicationContext)
        val sendFcmReceiptDTO = Gson().fromJson(receivedPushNotificationDTO.data, SendFcmReceiptDTO::class.java)
        FcmRepositoryImpl.subscribeToTopic(sendFcmReceiptDTO.roomId)

        notificationHelper.notifyNotification(applicationContext, sendFcmReceiptDTO)
    }

    /**
     * 새로운 정산방 알림
     */
    private fun newCalcRoom(receivedPushNotificationDTO: ReceivedPushNotificationDTO) {
        val notificationHelper = NewCalcRoomNotificationHelper.getINSTANCE(applicationContext)
        val sendFcmCalcRoomDTO = Gson().fromJson(receivedPushNotificationDTO.data, SendFcmCalcRoomDTO::class.java)

        notificationHelper.notifyNotification(applicationContext, sendFcmCalcRoomDTO)
    }

    /**
     * 정산 채촉
     */
    private fun calcRush(receivedPushNotificationDTO: ReceivedPushNotificationDTO) {
        val sendFcmCalcRushDTO = Gson().fromJson(receivedPushNotificationDTO.data, SendFcmCalcRushDTO::class.java)

        val notificationHelper = CalcRushNotificationHelper.getINSTANCE(applicationContext)
        notificationHelper.notifyNotification(applicationContext, sendFcmCalcRushDTO)


    }
}