package com.example.wapapp2.model.notifications

enum class NotificationType(val channelId: String) {
    NewCalcRoom("100"), Receipt("200"), Chat("300")
}