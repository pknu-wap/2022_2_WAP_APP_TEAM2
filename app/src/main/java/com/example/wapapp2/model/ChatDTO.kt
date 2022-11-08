package com.example.wapapp2.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ChatDTO(
        @Exclude
        val userName: String,
        @Exclude
        val userId: String,

        @ServerTimestamp
        val sendedTime: Date? = null,
        val msg: String,
        val senderId: String
)
