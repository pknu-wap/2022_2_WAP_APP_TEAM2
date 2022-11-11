package com.example.wapapp2.model

import com.google.firebase.firestore.Exclude

data class FriendDTO(
        val friendUserId: String,
        val alias: String,
        @Exclude
        val friendName: String,
        @get:Exclude
        val email: String
)
