package com.example.wapapp2.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

data class CalcRoomDTO(
        @ServerTimestamp
        val createdTime: Date? = null,
        val creatorUserId: String,
        val endReceiptIds: ArrayList<String>,
        val ongoingReceiptIds: ArrayList<String>,
        val participantIds: ArrayList<String>,
        val recentMsg: RecentMsg,
        val name: String,

        @Exclude
        val people: ArrayList<CalcRoomMemberData>
) {
    data class RecentMsg(
            val msg: String,
            val msgId: String,
            @ServerTimestamp
            val sendedTime: Date? = null,
            val senderId: String
    )
}
