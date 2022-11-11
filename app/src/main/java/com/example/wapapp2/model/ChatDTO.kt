package com.example.wapapp2.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

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
        constructor(parcel : Parcel) : this("", "", null, "", "")

        override fun describeContents(): Int {
                TODO("Not yet implemented")
        }

        override fun writeToParcel(dest: Parcel?, flags: Int) {
                TODO("Not yet implemented")
        }

        companion object CREATOR : Parcelable.Creator<ChatDTO> {
                override fun createFromParcel(parcel: Parcel): ChatDTO {
                        return ChatDTO(parcel)
                }

                override fun newArray(size: Int): Array<ChatDTO?> {
                        return arrayOfNulls(size)
                }
        }
}
