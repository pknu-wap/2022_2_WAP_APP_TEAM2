package com.example.wapapp2.model.notifications

import com.google.gson.annotations.SerializedName

data class PushNotificationDTO(
        @SerializedName("to")
        val to: String,
        @SerializedName("notification")
        val notification: Notification,
) {
    data class Notification(
            @SerializedName("title")
            val title: String,
            @SerializedName("body")
            val body: String,
    )


}