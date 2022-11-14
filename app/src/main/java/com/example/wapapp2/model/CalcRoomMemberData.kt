package com.example.wapapp2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalcRoomMemberData(val userId: String, val userName: String) : Parcelable
