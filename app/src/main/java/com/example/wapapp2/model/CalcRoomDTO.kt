package com.example.wapapp2.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

data class CalcRoomDTO(
        @ServerTimestamp
        @PropertyName("createdTime")
        var createdTime: Date? = null,
        @ServerTimestamp
        @PropertyName("lastModifiedTime")
        var lastModifiedTime: Date? = null,
        @PropertyName("creatorUserId")
        var creatorUserId: String,
        @PropertyName("endReceiptIds")
        var endReceiptIds: ArrayList<String>,
        @PropertyName("ongoingReceiptIds")
        var ongoingReceiptIds: ArrayList<String>,
        @PropertyName("participantIds")
        var participantIds: ArrayList<String>,
        @PropertyName("recentMsg")
        var recentMsg: RecentMsg,
        @PropertyName("name")
        var name: String,

        @Exclude
        val people: ArrayList<CalcRoomMemberData>
) {
    @Exclude
    var id: String? = "LvJY5fz6TjlTDaHHX53l"

    data class RecentMsg(
            @PropertyName("msg")
            var msg: String,
            @PropertyName("msgId")
            var msgId: String,
            @PropertyName("sendedTime")
            var sendedTime: Date? = null,
            @PropertyName("senderId")
            var senderId: String
    )
}
