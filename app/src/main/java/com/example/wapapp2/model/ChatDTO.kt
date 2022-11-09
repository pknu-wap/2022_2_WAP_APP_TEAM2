package com.example.wapapp2.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ChatDTO(
        @Exclude
        var userName: String,
        @Exclude
        var userId: String,
        @ServerTimestamp
        @PropertyName("sendedTime")
        var sendedTime: Date? = null,
        @PropertyName("msg")
        var msg: String,
        @PropertyName("senderId")
        var senderId: String
)
