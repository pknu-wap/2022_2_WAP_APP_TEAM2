package com.example.wapapp2.model.notifications.send

import com.example.wapapp2.model.ChatDTO
import com.google.gson.annotations.SerializedName

class SendFcmChatDTO(
        @SerializedName("chatDTO")
        val chatDTO: ChatDTO,
        @SerializedName("roomName")
        val roomName: String,
        @SerializedName("roomId")
        val roomId: String,
)
