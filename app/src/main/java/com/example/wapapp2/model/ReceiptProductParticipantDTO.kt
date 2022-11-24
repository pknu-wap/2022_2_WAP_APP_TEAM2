package com.example.wapapp2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceiptProductParticipantDTO(
        val userId: String,
        val userName: String,
        val myFriend: Boolean,
        val email: String,
        val fcmToken: String,
        var price: Int,
) : Parcelable
