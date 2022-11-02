package com.example.wapapp2.notification.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wapapp2.model.notifications.NotificationObj


abstract class AbstractNotificationHelper
protected constructor(context: Context, protected val channelId: String, protected val channelName: String, protected val
channelDescription:
String) {
    protected val notificationManager: NotificationManager

    init {
        notificationManager = context.getSystemService(NotificationManager::class.java)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)

                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    open fun createNotification(context: Context): NotificationObj {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel()

        val builder = NotificationCompat.Builder(context, channelId)
        return NotificationObj(notificationId = System.currentTimeMillis().toInt(), notificationBuilder = builder)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun notifyNotification(notificationObj: NotificationObj) {
        notificationManager.notify(notificationObj.notificationId, notificationObj.notificationBuilder.build())
    }


}