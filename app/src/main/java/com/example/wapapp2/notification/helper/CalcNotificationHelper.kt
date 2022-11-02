package com.example.wapapp2.notification.helper

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wapapp2.R
import com.example.wapapp2.model.notifications.NotificationObj
import com.example.wapapp2.model.notifications.NotificationType

class CalcNotificationHelper private constructor(context: Context) :
        AbstractNotificationHelper(context, context.getString(R.string.calc_notification_channel_id), context.getString(R.string
                .calc_notification_channel_name),
                context.getString(R.string.calc_notification_channel_description)) {

    companion object {
        private var INSTANCE: CalcNotificationHelper? = null

        fun getINSTANCE(context: Context): CalcNotificationHelper {
            if (INSTANCE == null)
                INSTANCE = CalcNotificationHelper(context)
            return INSTANCE!!
        }
    }


    override fun createNotification(context: Context): NotificationObj {
        val notificationObj = super.createNotification(context)
        notificationObj.notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationObj.notificationBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        notificationObj.notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return notificationObj
    }

}