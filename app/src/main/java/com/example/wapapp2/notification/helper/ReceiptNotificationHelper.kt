package com.example.wapapp2.notification.helper

import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.wapapp2.R
import com.example.wapapp2.model.notifications.NotificationObj
import com.example.wapapp2.model.notifications.send.SendFcmReceiptDTO

class ReceiptNotificationHelper private constructor(context: Context)
    : AbstractNotificationHelper(context, context.getString(R.string.receipt_notification_channel_id), context.getString(R.string.receipt_notification_channel_name),
        context.getString(R.string.receipt_notification_channel_description)) {

    companion object {
        private var INSTANCE: ReceiptNotificationHelper? = null

        fun getINSTANCE(context: Context): ReceiptNotificationHelper {
            if (INSTANCE == null)
                INSTANCE = ReceiptNotificationHelper(context)
            return INSTANCE!!
        }
    }

    override fun createNotification(context: Context): NotificationObj {
        val notificationObj = super.createNotification(context)
        notificationObj.notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationObj.notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return notificationObj
    }


    fun notifyNotification(context: Context, sendFcmReceiptDTO: SendFcmReceiptDTO) {
        val remoteViews = RemoteViews(context.packageName, R.layout.calculation_notification_expanded_remoteviews)
    }

}