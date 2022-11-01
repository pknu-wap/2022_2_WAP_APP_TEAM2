package com.example.wapapp2.dummy

import android.content.Context
import android.widget.RemoteViews
import com.example.wapapp2.R
import com.example.wapapp2.notification.helper.CalcNotificationHelper
import com.example.wapapp2.notification.helper.ChatNotificationHelper
import com.example.wapapp2.notification.helper.ReceiptNotificationHelper

class TestLogics private constructor() {
    companion object {
        fun notifyCalcNotification(context: Context) {
            val helper = CalcNotificationHelper.getINSTANCE(context)
            val notificationObj = helper.createNotification(context)
            notificationObj.notificationBuilder.setCustomContentView(RemoteViews(context.packageName, R.layout.calculation_notification_remoteviews))
            notificationObj.notificationBuilder.setShowWhen(false)
            helper.notifyNotification(notificationObj)
        }


        fun notifyChatNotification(context: Context) {
            val helper = ChatNotificationHelper.getINSTANCE(context)
            val notificationObj = helper.createNotification(context)
            notificationObj.notificationBuilder.setCustomContentView(RemoteViews(context.packageName, R.layout.chat_notification_remoteviews))
            helper.notifyNotification(notificationObj)
        }

        fun notifyReceiptNotification(context: Context) {
            val helper = ReceiptNotificationHelper.getINSTANCE(context)
            val notificationObj = helper.createNotification(context)
            notificationObj.notificationBuilder.setCustomContentView(RemoteViews(context.packageName, R.layout.calculation_notification_remoteviews))
            helper.notifyNotification(notificationObj)
        }
    }
}