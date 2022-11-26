package com.example.wapapp2.notification.helper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DeviceUtils
import com.example.wapapp2.main.MainActivity
import com.example.wapapp2.model.notifications.NotificationObj
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.send.SendFcmChatDTO

class ChatNotificationHelper private constructor(context: Context)
    : AbstractNotificationHelper(context, NotificationType.Chat.channelId, context.getString(R.string.chat_notification_channel_name),
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
        notificationObj.notificationBuilder.setShowWhen(true)

        return notificationObj
    }

    fun notifyNotification(context: Context, sendFcmChatDTO: SendFcmChatDTO) {
        val notificationObj = createNotification(context)

        // 클릭시 채팅방 뜨도록 구현할 예정. ( 미완 )
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_ONE_SHOT

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, flags)
        notificationObj.notificationBuilder.setContentIntent(pendingIntent)

        val remoteViews = RemoteViews(context.packageName, R.layout.chat_notification_remoteviews)
        remoteViews.setTextViewText(R.id.member_name, sendFcmChatDTO.chatDTO.userName)
        remoteViews.setTextViewText(R.id.msg, sendFcmChatDTO.chatDTO.msg)

        notificationObj.notificationBuilder.setContentTitle(sendFcmChatDTO.chatDTO.userName)
        notificationObj.notificationBuilder.setContentText(sendFcmChatDTO.chatDTO.msg)
        notificationObj.notificationBuilder.setLargeIcon(ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24)
                ?.toBitmap(config = Bitmap.Config.ARGB_8888))
        notifyNotification(notificationObj)

        DeviceUtils.wakeLock(context)
    }

}