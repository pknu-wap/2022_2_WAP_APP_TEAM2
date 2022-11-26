package com.example.wapapp2.model.notifications

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class NotificationType(val channelId: String) : Parcelable {
    NewCalcRoom("100"), Receipt("200"), Chat("300")
}