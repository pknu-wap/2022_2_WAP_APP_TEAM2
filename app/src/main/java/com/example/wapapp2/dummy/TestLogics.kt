package com.example.wapapp2.dummy

import android.content.Context
import android.widget.RemoteViews
import com.example.wapapp2.R
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.notification.helper.CalcNotificationHelper
import com.example.wapapp2.notification.helper.ChatNotificationHelper
import com.example.wapapp2.notification.helper.ReceiptNotificationHelper

class TestLogics private constructor() {
    companion object {
        fun notifyCalcNotification(context: Context) {
            val helper = CalcNotificationHelper.getINSTANCE(context)
            helper.notifyNotification(context, ReceiptDTO("0", "정산 참여 부탁해요", "0"), FriendDTO("0", "옥수환", "" +
                    "test@gmail.com"))
        }


        fun notifyChatNotification(context: Context) {
            val helper = ChatNotificationHelper.getINSTANCE(context)
            helper.notifyNotification(context, "정산1", ChatDTO("남진화", "0", "", "반갑습니다!", R.drawable.ic_baseline_person_24))
        }

        fun notifyReceiptNotification(context: Context) {
            val helper = ReceiptNotificationHelper.getINSTANCE(context)
            val notificationObj = helper.createNotification(context)
            notificationObj.notificationBuilder.setCustomContentView(RemoteViews(context.packageName, R.layout.calculation_notification_expanded_remoteviews))
            helper.notifyNotification(notificationObj)
        }
    }
}