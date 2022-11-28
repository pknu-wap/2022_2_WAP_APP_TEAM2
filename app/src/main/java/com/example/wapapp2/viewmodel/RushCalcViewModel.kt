package com.example.wapapp2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.CalcRoomParticipantDTO
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.send.SendFcmCalcRushDTO
import com.example.wapapp2.repository.FcmRepositoryImpl
import com.example.wapapp2.view.calculation.rushcalc.ICheckedRecipient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RushCalcViewModel : ViewModel(), ICheckedRecipient<CalcRoomParticipantDTO> {
    val selectedRecipients = mutableSetOf<String>()

    lateinit var myUid: String
    lateinit var myUserName: String
    lateinit var calcRoomId: String

    override fun onCheckedChange(e: CalcRoomParticipantDTO, isChecked: Boolean) {
        if (isChecked) {
            selectedRecipients.add(e.fcmToken)
        } else {
            selectedRecipients.remove(e.fcmToken)
        }
    }


    /**
     * 정산 재촉 알림 보내기
     */
    fun sendRushCalcFcm() {
        CoroutineScope(Dispatchers.IO).launch {
            FcmRepositoryImpl.sendFcmToMultipleDevices(NotificationType.CalcRush, selectedRecipients.toMutableList(), SendFcmCalcRushDTO(calcRoomId,
                    myUserName, myUid))
        }
    }

}