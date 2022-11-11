package com.example.wapapp2.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendDTO(
        var friendUserId: String,
        var alias: String,
        @get:Exclude
        var friendName: String,
        @get:Exclude
        var email: String
) : Parcelable {
    constructor() : this("", "", "", "")
}