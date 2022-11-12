package com.example.wapapp2.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ChatDTO(
        @get:Exclude
        var userName: String,
        @get:Exclude
        var userId: String,
        @ServerTimestamp
        @PropertyName("sendedTime")
        var sendedTime: Date? = null,
        @PropertyName("msg")
        var msg: String,
        @PropertyName("senderId")
        var senderId: String
) : Parcelable {
        constructor() : this("", "", null, "", "")
}
