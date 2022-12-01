package com.example.wapapp2.retrofit

import com.example.wapapp2.model.notifications.TokenPushNotificationDTO
import com.example.wapapp2.model.notifications.TopicPushNotificationDTO
import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitQueries {
    @Headers(
            "Content-Type:${RetrofitClient.FCM_CONTENT_TYPE}",
            "Authorization: key=${RetrofitClient.FCM_SERVER_KEY}"
    )
    @POST("fcm/send")
    suspend fun sendFcmToTopic(@Body jsonObject: TopicPushNotificationDTO): Response<JsonElement>

    @Headers(
            "Content-Type:${RetrofitClient.FCM_CONTENT_TYPE}",
            "Authorization: key=${RetrofitClient.FCM_SERVER_KEY}"
    )
    @POST("fcm/send")
    suspend fun sendFcmToRecipient(@Body jsonObject: TokenPushNotificationDTO): Response<JsonElement>
}