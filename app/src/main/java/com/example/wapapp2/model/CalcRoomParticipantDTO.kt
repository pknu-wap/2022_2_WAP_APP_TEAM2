package com.example.wapapp2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalcRoomParticipantDTO(val userId: String, val userName: String, val myFriend: Boolean, val email: String) : Parcelable
