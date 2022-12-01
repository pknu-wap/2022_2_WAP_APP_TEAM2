package com.example.wapapp2.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ChatDTO(
        @PropertyName("userName")
        @SerializedName("userName")
        var userName: String,

        @ServerTimestamp
        @PropertyName("sendedTime")
        @SerializedName("sendedTime")
        var sendedTime: Date? = null,

        @PropertyName("msg")
        @SerializedName("msg")
        var msg: String,

        @SerializedName("senderId")
        @PropertyName("senderId")
        var senderId: String,

        @SerializedName("notice")
        @PropertyName("notice")
        var isNotice: Boolean,
) : Parcelable {
    constructor() : this("", null, "", "", false)

    @get:Exclude
    var id: String? = null
}
