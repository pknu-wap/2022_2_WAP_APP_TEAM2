package com.example.wapapp2.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceiptProductParticipantDTO(
        @get:Exclude
        val userId: String,
        @PropertyName("userName")
        val userName: String,
        @get:Exclude
        var myFriend: Boolean,
        @get:Exclude
        var fcmToken: String,
) : Parcelable {
    constructor() : this("", "", false, "")
}
