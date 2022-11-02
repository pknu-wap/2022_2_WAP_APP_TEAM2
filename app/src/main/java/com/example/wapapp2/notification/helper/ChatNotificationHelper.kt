package com.example.wapapp2.notification.helper

import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.wapapp2.R
import com.example.wapapp2.model.notifications.NotificationObj

class ChatNotificationHelper private constructor(context: Context)
    : AbstractNotificationHelper(context, context.getString(R.string.chat_notification_channel_id), context.getString(R.string.chat_notification_channel_name),
        context.getString(R.string.chat_notification_channel_description)) {

    companion object {
        private var INSTANCE: ChatNotificationHelper? = null

        fun getINSTANCE(context: Context): ChatNotificationHelper {
            if (INSTANCE == null)
                INSTANCE = ChatNotificationHelper(context)
            return INSTANCE!!
        }
    }

    override fun createNotification(context: Context): NotificationObj {
        val notificationObj = super.createNotification(context)
        notificationObj.notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationObj.notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return notificationObj
    }


}