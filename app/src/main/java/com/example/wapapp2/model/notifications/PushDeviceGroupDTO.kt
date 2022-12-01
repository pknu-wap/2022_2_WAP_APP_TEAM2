package com.example.wapapp2.model.notifications

import com.google.gson.annotations.SerializedName

data class PushDeviceGroupDTO(
        @SerializedName("operation")
        val operation: String = "create",
        @SerializedName("notification_key_name")
        var notification_key_name: String,
        @SerializedName("registration_ids")
        var registration_ids: MutableList<String> = mutableListOf(),
)
