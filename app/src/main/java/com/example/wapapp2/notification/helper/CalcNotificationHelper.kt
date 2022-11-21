package com.example.wapapp2.notification.helper

import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.wapapp2.R
import com.example.wapapp2.model.notifications.NotificationObj
import com.example.wapapp2.model.notifications.send.SendFcmReceiptDTO
import com.example.wapapp2.repository.ReceiptImgRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

    fun notifyNotification(context: Context, sendFcmReceiptDTO: SendFcmReceiptDTO) {
        // 영수증 사진 다운로드, 영수증 사진 없으면 사진 미표시
        if (!sendFcmReceiptDTO.receiptDTO.imgUrl.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.Default).launch {
                val imgResult = async {
                    ReceiptImgRepositoryImpl.INSTANCE.downloadReceiptImg(sendFcmReceiptDTO.receiptDTO.imgUrl!!)
                }
                imgResult.await()
                if (imgResult.getCompletionExceptionOrNull() == null) {
                    sendFcmReceiptDTO.receiptDTO.receiptImgBitmap = imgResult.await()
                }
                createNotification(context, sendFcmReceiptDTO)
            }
        } else {
            createNotification(context, sendFcmReceiptDTO)
        }

    }

    private fun createNotification(context: Context, sendFcmReceiptDTO: SendFcmReceiptDTO) {
        val receiptDTO = sendFcmReceiptDTO.receiptDTO

        val collapsedRemoteViews = RemoteViews(context.packageName, R.layout.calculation_notification_collapsed_remoteviews)
        collapsedRemoteViews.setTextViewText(R.id.payer, sendFcmReceiptDTO.payersName)
        collapsedRemoteViews.setTextViewText(R.id.total_money, receiptDTO.totalMoney.toString() + "원")
        collapsedRemoteViews.setTextViewText(R.id.msg, receiptDTO.name)

        val expandedRemoteViews = RemoteViews(context.packageName, R.layout.calculation_notification_expanded_remoteviews)
        expandedRemoteViews.setTextViewText(R.id.payer, sendFcmReceiptDTO.payersName)
        expandedRemoteViews.setTextViewText(R.id.date_time, DateTime.parse(receiptDTO.date).toString(dateTimeFormat))
        expandedRemoteViews.setTextViewText(R.id.total_money, receiptDTO.totalMoney.toString() + "원")
        expandedRemoteViews.setTextViewText(R.id.msg, receiptDTO.name)

        sendFcmReceiptDTO.receiptDTO.receiptImgBitmap?.apply {
            collapsedRemoteViews.setImageViewBitmap(R.id.receipt_image, this)
            expandedRemoteViews.setImageViewBitmap(R.id.receipt_image, this)

        }

        val notificationObj = createNotification(context)

        notificationObj.notificationBuilder.setCustomContentView(collapsedRemoteViews)
        notificationObj.notificationBuilder.setCustomBigContentView(expandedRemoteViews)
        notifyNotification(notificationObj)
    }
}