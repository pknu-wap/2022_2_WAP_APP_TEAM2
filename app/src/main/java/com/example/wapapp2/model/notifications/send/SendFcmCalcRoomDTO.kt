package com.example.wapapp2.model.notifications.send

import android.graphics.Bitmap
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.model.ReceiptDTO
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import java.util.*

data class SendFcmCalcRoomDTO(
        @SerializedName("roomId")
        val roomId: String,
)
