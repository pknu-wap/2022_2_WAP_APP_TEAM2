package com.example.wapapp2.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class CalcRoomDTO(
        @ServerTimestamp
        @PropertyName("createdTime")
        var createdTime: Date?,
        @ServerTimestamp
        @PropertyName("lastModifiedTime")
        var lastModifiedTime: Date?,
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
        @field:JvmField
        @PropertyName("calculationStatus")
        var calculationStatus: Boolean,   // true if "정산 완료/ 정산항목 없음", false if "정산진행중.."
        @get:Exclude
        val people: ArrayList<CalcRoomParticipantDTO>,
) : Parcelable {
    constructor() : this(null, null, "", arrayListOf(), arrayListOf(), arrayListOf(), RecentMsg(msg = "", msgId = "", sendedTime = null,
            senderId = ""), "", true, arrayListOf())

    @get:Exclude
    var id: String? = null


    @Parcelize
    data class RecentMsg(
            @PropertyName("msg")
            var msg: String,
            @PropertyName("msgId")
            var msgId: String,
            @PropertyName("sendedTime")
            var sendedTime: Date? = null,
            @PropertyName("senderId")
            var senderId: String,
    ) : Parcelable {
        constructor() : this("", "", null, "")
    }


}
