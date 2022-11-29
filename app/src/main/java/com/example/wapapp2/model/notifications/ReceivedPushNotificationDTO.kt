package com.example.wapapp2.model.notifications

import android.os.Parcelable
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ReceivedPushNotificationDTO(
        @SerializedName("type")
        val type: NotificationType,
        @SerializedName("calcRoomId")
        val calcRoomId: String,
        @SerializedName("data")
        val data: JsonObject,
)
