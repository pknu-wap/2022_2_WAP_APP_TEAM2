package com.example.wapapp2.notification.helper

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DeviceUtils
import com.example.wapapp2.model.notifications.NotificationObj
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.send.SendFcmReceiptDTO
import com.example.wapapp2.repository.ReceiptImgRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class ReceiptNotificationHelper private constructor(context: Context)
    : AbstractNotificationHelper(context, NotificationType.Receipt.channelId, context.getString(R.string.receipt_notification_channel_name),
        context.getString(R.string.receipt_notification_channel_description)) {
    private val dateTimeFormat = "yyyy.MM.dd E a hh:mm"

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
        notificationObj.notificationBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        notificationObj.notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return notificationObj
    }

    fun notifyNotification(context: Context, sendFcmReceiptDTO: SendFcmReceiptDTO) {
        // 영수증 사진 다운로드, 영수증 사진 없으면 사진 미표시
        if (!sendFcmReceiptDTO.imgUrl.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.Default).launch {
                val imgResult = async {
                    ReceiptImgRepositoryImpl.INSTANCE.downloadReceiptImg(sendFcmReceiptDTO.imgUrl)
                }
                imgResult.await()?.apply {
                    sendFcmReceiptDTO.receiptImgBitmap = this
                }
                createNotification(context, sendFcmReceiptDTO)
            }
        } else {
            createNotification(context, sendFcmReceiptDTO)
        }

    }

    private fun createNotification(context: Context, sendFcmReceiptDTO: SendFcmReceiptDTO) {

        val collapsedRemoteViews = RemoteViews(context.packageName, R.layout.calculation_notification_collapsed_remoteviews)
        collapsedRemoteViews.setTextViewText(R.id.payer, sendFcmReceiptDTO.payersId)
        collapsedRemoteViews.setTextViewText(R.id.total_money, sendFcmReceiptDTO.totalMoney.toString() + "원")
        collapsedRemoteViews.setTextViewText(R.id.msg, sendFcmReceiptDTO.name)

        val expandedRemoteViews = RemoteViews(context.packageName, R.layout.calculation_notification_expanded_remoteviews)
        expandedRemoteViews.setTextViewText(R.id.payer, sendFcmReceiptDTO.payersId)
        expandedRemoteViews.setTextViewText(R.id.date_time, DateTime.parse(sendFcmReceiptDTO.createdTime).toString(dateTimeFormat))
        expandedRemoteViews.setTextViewText(R.id.total_money, sendFcmReceiptDTO.totalMoney.toString() + "원")
        expandedRemoteViews.setTextViewText(R.id.msg, sendFcmReceiptDTO.name)

        sendFcmReceiptDTO.receiptImgBitmap?.apply {
            collapsedRemoteViews.setImageViewBitmap(R.id.receipt_image, this)
            expandedRemoteViews.setImageViewBitmap(R.id.receipt_image, this)
        }

        if (sendFcmReceiptDTO.receiptImgBitmap == null) {
            collapsedRemoteViews.setViewVisibility(R.id.receipt_image, View.GONE)
            expandedRemoteViews.setViewVisibility(R.id.receipt_image, View.GONE)
        }

        val notificationObj = createNotification(context)

        notificationObj.notificationBuilder.setCustomContentView(collapsedRemoteViews)
        notificationObj.notificationBuilder.setCustomBigContentView(expandedRemoteViews)
        notifyNotification(notificationObj)

        DeviceUtils.wakeLock(context)
    }
}