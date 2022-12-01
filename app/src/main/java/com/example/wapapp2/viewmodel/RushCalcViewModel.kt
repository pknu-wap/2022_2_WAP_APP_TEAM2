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
    val selectedRecipientTokens = mutableSetOf<String>()
    val calcRoomParticipants = mutableMapOf<String, CalcRoomParticipantDTO>()

    lateinit var myUid: String
    lateinit var myUserName: String
    lateinit var calcRoomId: String

    override fun onCheckedChange(e: CalcRoomParticipantDTO, isChecked: Boolean) {
        if (isChecked) {
            selectedRecipientTokens.add(e.fcmToken)
        } else {
            selectedRecipientTokens.remove(e.fcmToken)
        }
    }


    /**
     * 정산 재촉 알림 보내기
     */
    fun sendRushCalcFcm() {
        CoroutineScope(Dispatchers.IO).launch {
            FcmRepositoryImpl.sendFcmToRecipients(NotificationType.CalcRush, selectedRecipientTokens.toMutableList(), SendFcmCalcRushDTO(calcRoomId,
                    myUserName, myUid))
        }
    }

}