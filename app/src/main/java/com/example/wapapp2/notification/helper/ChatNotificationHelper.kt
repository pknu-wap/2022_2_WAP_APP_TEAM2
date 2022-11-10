package com.example.wapapp2.notification.helper

import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.wapapp2.R
import com.example.wapapp2.model.ChatDTO
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

    fun notifyNotification(context: Context, roomName: String, chatDTO: ChatDTO) {
        val notificationObj = createNotification(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.chat_notification_remoteviews)
        remoteViews.setTextViewText(R.id.member_name, chatDTO.userName)
        remoteViews.setTextViewText(R.id.msg, chatDTO.msg)

        notificationObj.notificationBuilder.setContentTitle(roomName)
        notificationObj.notificationBuilder.setCustomContentView(remoteViews)
        notifyNotification(notificationObj)
    }

}