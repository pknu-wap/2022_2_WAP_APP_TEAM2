package com.example.wapapp2.repository

import com.example.wapapp2.model.notifications.PushNotificationDTO
import com.example.wapapp2.repository.interfaces.FcmRepository
import com.example.wapapp2.retrofit.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FcmRepositoryImpl : FcmRepository {
    override suspend fun sendMessage(notificaiton: PushNotificationDTO) {
        val response = RetrofitClient.getClients().sendFcm(notificaiton)
    }

    override suspend fun sendCalculation(notificaiton: PushNotificationDTO) {
        val response = RetrofitClient.getClients().sendFcm(notificaiton)
    }

    override fun subscribeToCalcRoomChat(roomId: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(roomId).addOnCompleteListener {
            if (it.isSuccessful) {

            }
        }
    }


    override fun unSubscribeToCalcRoomChat(roomId: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(roomId)
    }


}