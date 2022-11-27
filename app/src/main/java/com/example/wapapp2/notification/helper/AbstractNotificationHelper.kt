package com.example.wapapp2.notification.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.wapapp2.main.MainActivity
import com.example.wapapp2.model.notifications.NotificationObj
import com.example.wapapp2.model.notifications.NotificationType


abstract class AbstractNotificationHelper
protected constructor(
        context: Context, protected val channelId: String, protected val channelName: String,
        protected val
        channelDescription:
        String,
) {
    protected val notificationManager: NotificationManager

    init {
        notificationManager = context.getSystemService(NotificationManager::class.java)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

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

    fun createActivityIntent(context: Context, arguments: Bundle): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtras(arguments)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }
}