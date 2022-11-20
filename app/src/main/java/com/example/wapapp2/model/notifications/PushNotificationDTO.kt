package com.example.wapapp2.model.notifications

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PushNotificationDTO(val receiverIds: MutableList<String>, var data: Data) : Parcelable {

    @Parcelize
    data class Data(var title: String?, var body: String?) : Parcelable
}
