package com.example.wapapp2.notification.helper

import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DeviceUtils
import com.example.wapapp2.model.notifications.NotificationObj
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.send.SendFcmCalcRushDTO
import com.example.wapapp2.repository.FriendsLocalRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalcRushNotificationHelper private constructor(context: Context) :
        AbstractNotificationHelper(context, NotificationType.CalcRush.channelId, context.getString(R.string
                .calc_notification_channel_name), context.getString(R.string.calc_notification_channel_description)) {


    companion object {
        private var INSTANCE: CalcRushNotificationHelper? = null

        fun getINSTANCE(context: Context): CalcRushNotificationHelper {
            if (INSTANCE == null)
                INSTANCE = CalcRushNotificationHelper(context)
            return INSTANCE!!
        }
    }


    override fun createNotification(context: Context): NotificationObj {
        val notificationObj = super.createNotification(context)
        notificationObj.notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationObj.notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        notificationObj.notificationBuilder.setAutoCancel(true)

        return notificationObj
    }

    fun notifyNotification(context: Context, sendFcmCalcRushDTO: SendFcmCalcRushDTO) {
        val notificationObj = createNotification(context)

        notificationObj.notificationBuilder.setContentTitle(context.getString(R.string.calc_rush))

        val localFriendsLocalRepository = FriendsLocalRepositoryImpl.getINSTANCE()
        CoroutineScope(Dispatchers.IO).launch {
            localFriendsLocalRepository.get(sendFcmCalcRushDTO.payersId).collectIndexed { index, value ->
                val alias = value?.alias ?: sendFcmCalcRushDTO.payersName

                val contentText = "${alias}님이 정산 요청을 하였습니다!"
                notificationObj.notificationBuilder.setContentText(contentText)

                val arguments = Bundle().apply {
                    putParcelable("notificationType", NotificationType.CalcRush)
                    putString("calcRoomId", sendFcmCalcRushDTO.roomId)
                }

                notificationObj.notificationBuilder.setContentIntent(createActivityIntent(context, arguments))

                withContext(Main) {
                    notifyNotification(notificationObj)
                    DeviceUtils.wakeLock(context)
                }
            }
        }


    }


}