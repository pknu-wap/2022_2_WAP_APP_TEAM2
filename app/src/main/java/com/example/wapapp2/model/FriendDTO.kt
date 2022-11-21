package com.example.wapapp2.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class FriendDTO(
        @PropertyName("friendUserId")
        @PrimaryKey
        var friendUserId: String,
        @PropertyName("alias")
        var alias: String,
) : Parcelable {
    constructor() : this("", "")
}