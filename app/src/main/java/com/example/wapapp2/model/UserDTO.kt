package com.example.wapapp2.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDTO(
        @get:Exclude
        var id: String,
        @PropertyName("email")
        var email: String,
        @PropertyName("gender")
        var gender: String,
        @PropertyName("imgUri")
        var imgUri: String,
        @PropertyName("name")
        var name: String,
        @PropertyName("fcmToken")
        var fcmToken: String,
        @PropertyName("myCalcRoomIds")
        val myCalcRoomIds: ArrayList<String>,
        @PropertyName("checkReceiptIds")
        val checkReceiptIds: ArrayList<String>,
        @PropertyName("completedReceiptIds")
        val completedReceiptIds: ArrayList<String>,
        @PropertyName("fixedReceiptIds")
        val fixedReceiptIds: ArrayList<String>,
) : Parcelable {
    constructor() : this("", "", "", "", "", "", arrayListOf(), arrayListOf(), arrayListOf(),
            arrayListOf())
}
