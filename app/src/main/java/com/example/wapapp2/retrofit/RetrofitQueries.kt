package com.example.wapapp2.retrofit

import com.example.wapapp2.model.notifications.PushNotificationDTO
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
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
    suspend fun sendFcm(@Body jsonObject: PushNotificationDTO): Response<JsonElement>
}