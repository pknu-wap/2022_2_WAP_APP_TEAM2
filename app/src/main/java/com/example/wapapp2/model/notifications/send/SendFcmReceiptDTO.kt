package com.example.wapapp2.model.notifications.send

import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.model.ReceiptDTO
import com.google.gson.annotations.SerializedName

data class SendFcmReceiptDTO(
        @SerializedName("receiptDTO")
        val receiptDTO: ReceiptDTO,
        @SerializedName("roomId")
        val roomId: String,
        @SerializedName("payersName")
        val payersName: String,
)
