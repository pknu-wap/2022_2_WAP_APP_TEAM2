package com.example.wapapp2.model.notifications.send

import android.graphics.Bitmap
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.model.ReceiptDTO
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import java.util.*

data class SendFcmReceiptDTO(
        @SerializedName("createdTime")
        val createdTime: String,
        @SerializedName("payersId")
        val payersId: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("totalMoney")
        val totalMoney: Int,
        @SerializedName("imgUrl")
        val imgUrl: String?,
        @SerializedName("roomId")
        val roomId: String,

        @Expose(serialize = false, deserialize = false)
        var receiptImgBitmap: Bitmap?,
)
