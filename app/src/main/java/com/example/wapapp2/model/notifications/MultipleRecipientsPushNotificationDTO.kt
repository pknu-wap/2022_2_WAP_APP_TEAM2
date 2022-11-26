package com.example.wapapp2.model.notifications

import com.google.gson.annotations.SerializedName

data class MultipleRecipientsPushNotificationDTO(
        @SerializedName("data")
        val data: Notification,
        @SerializedName("registration_ids")
        val recipientIds: MutableList<String>,
) {
    data class Notification(
            @SerializedName("title")
            val title: String,
            @SerializedName("body")
            val body: String,
    )


}