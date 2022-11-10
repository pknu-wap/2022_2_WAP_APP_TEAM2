package com.example.wapapp2.notification.helper

import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.wapapp2.R
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.notifications.NotificationObj
import org.joda.time.DateTime

class CalcNotificationHelper private constructor(context: Context) :
        AbstractNotificationHelper(context, context.getString(R.string.calc_notification_channel_id), context.getString(R.string
                .calc_notification_channel_name),
                context.getString(R.string.calc_notification_channel_description)) {

    private val dateTimeFormat = "yyyy.MM.dd E a hh:mm"

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

    fun notifyNotification(context: Context, receiptDTO: ReceiptDTO, friendDTO: FriendDTO) {
        val notificationObj = createNotification(context)
        val collapsedRemoteViews = RemoteViews(context.packageName, R.layout.calculation_notification_collapsed_remoteviews)
        collapsedRemoteViews.setTextViewText(R.id.payer, friendDTO.friendName)
        collapsedRemoteViews.setTextViewText(R.id.total_money, receiptDTO.totalMoney.toString() + "원")
        collapsedRemoteViews.setTextViewText(R.id.msg, receiptDTO.name)
        collapsedRemoteViews.setImageViewResource(R.id.receipt_image, R.drawable.receipt_sample)

        val expandedRemoteViews = RemoteViews(context.packageName, R.layout.calculation_notification_expanded_remoteviews)
        expandedRemoteViews.setTextViewText(R.id.payer, friendDTO.friendName)
        expandedRemoteViews.setTextViewText(R.id.date_time, DateTime.parse(receiptDTO.date).toString(dateTimeFormat))
        expandedRemoteViews.setTextViewText(R.id.total_money, receiptDTO.totalMoney.toString() + "원")
        expandedRemoteViews.setTextViewText(R.id.msg, receiptDTO.name)
        expandedRemoteViews.setImageViewResource(R.id.receipt_image, R.drawable.receipt_sample)

        notificationObj.notificationBuilder.setCustomContentView(collapsedRemoteViews)
        notificationObj.notificationBuilder.setCustomBigContentView(expandedRemoteViews)
        notifyNotification(notificationObj)
    }

}