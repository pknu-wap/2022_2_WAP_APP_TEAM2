package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.repository.interfaces.CalcRoomRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CalcRoomRepositorylmpl private constructor() : CalcRoomRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private var INSTANCE: CalcRoomRepositorylmpl? = null

        fun getINSTANCE() = INSTANCE!!

        fun initialize() {
            INSTANCE = CalcRoomRepositorylmpl()
        }
    }

    /**
     * 정산방 생성
     */
    override suspend fun addNewCalcRoom(calcRoomDTO: CalcRoomDTO) = suspendCoroutine<Boolean> { continuation ->
        val newDocument = firestore
                .collection(FireStoreNames.calc_rooms.name)
                .document()

        //calcRoom collection에 저장
        newDocument.set(calcRoomDTO).addOnCompleteListener {
            if (it.isSuccessful) {
                CoroutineScope(Dispatchers.Default).launch {
                    calcRoomDTO.id = newDocument.id // 참여 정산방목록으로 저장
                    val results = ArrayList<Deferred<Boolean>>()

                    // user collection에 참여방id 저장
                    for (participant in calcRoomDTO.participantIds)
                        results.add(async { addRoomID_toParticipant(participant, calcRoomDTO.id.toString()) })

                    if (false in results.awaitAll()) continuation.resume(false)
                    else continuation.resume(true)
                }
            } else
                continuation.resume(false)
        }
    }

    override suspend fun getOngoingReceiptCounts(calcRoomId: String): Int = suspendCoroutine<Int> { continuation ->
        val document = firestore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val dto = it.result.toObject<CalcRoomDTO>()!!
                        continuation.resume(dto.ongoingReceiptIds.size)
                    } else
                        continuation.resume(0)
                }
    }

    override suspend fun updateCalculationStatus(
            calcRoomId: String, status: Boolean,
            receiptIds: MutableList<String>,
    ) = suspendCoroutine<Boolean> { continuation ->
        val map = mutableMapOf<String, Any>()
        map["calculationStatus"] = status

        if (!status) {
            map["ongoingReceiptIds"] = mutableListOf<String>()
            map["endReceiptIds"] = receiptIds
        }

        val document = firestore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).update(map)
                .addOnCompleteListener {
                    continuation.resume(it.isSuccessful)
                }
    }

    /**
     * 정산방 데이터 실시간 감시
     */
    override fun snapshotCalcRoom(
            roomId: String, listener: EventListener<DocumentSnapshot>,
    ): ListenerRegistration = firestore.collection(FireStoreNames.calc_rooms.name)
            .document(roomId).addSnapshotListener(listener)


    /**
     * 정산방 정보 가져오기
     */
    override suspend fun getCalcRoom(roomId: String) = suspendCoroutine<CalcRoomDTO> { continuation ->
        val document = firestore.collection(FireStoreNames.calc_rooms.name)
                .document(roomId)
        document.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val dto = it.result.toObject<CalcRoomDTO>()!!
                continuation.resume(dto)
            } else {
                continuation.resumeWithException(it.exception!!)
            }
        }
    }

    /**
     * 정산방에서 나가기 -> 남아있는 인원 없으면 정산방 삭제
     */
    override suspend fun exitFromCalcRoom(roomId: String) = suspendCoroutine<Boolean> { continuation ->
        val myId = auth.currentUser!!.uid
        val roomDocument = firestore.collection(FireStoreNames.calc_rooms.name)
                .document(roomId)

        roomDocument.update("participantIds", FieldValue.arrayRemove(myId))
                .addOnCompleteListener {
                    roomDocument.get().addOnCompleteListener() {
                        if (it.isSuccessful) {
                            if ((it.result["participantIds"] as List<String>).size <= 0)
                                roomDocument.delete().addOnCompleteListener { continuation.resume(it.isSuccessful) }
                            else
                                continuation.resume(it.isSuccessful)
                        } else
                            continuation.resume(it.isSuccessful)

                    }


                }
    }

    /**
     * 내가 참여중인 정산방 id목록 가져오기
     */
    override fun getMyCalcRoomIds(snapshotListener: EventListener<DocumentSnapshot>): ListenerRegistration =
            firestore.collection(FireStoreNames.users.name).document(auth.currentUser!!.uid)
                    .addSnapshotListener(snapshotListener)


    /**
     * 정산방에 친구 초대
     */
    override suspend fun inviteFriends(list: MutableList<FriendDTO>, roomId: String) {
        //calcroom문서에 참여자id추가
        val idArr = arrayListOf<String>()
        for (f in list) {
            idArr.add(f.friendUserId)
        }

        firestore.collection(FireStoreNames.calc_rooms.name).document(roomId)
                .update("participantIds", FieldValue.arrayUnion(*idArr.toArray()))

        //user문서에 정산방 id추가
        val collection = firestore.collection(FireStoreNames.users.name)

        firestore.runBatch { batch ->
            for (friend in list) {
                batch.update(collection.document(friend.friendUserId), "myCalcRoomIds", FieldValue.arrayUnion(roomId))
            }
        }
    }


    private suspend fun addRoomID_toParticipant(participant: String, roomId: String) = suspendCoroutine<Boolean> { continuation ->
        CoroutineScope(Dispatchers.Main).launch {
            val participantDocument = firestore.collection(FireStoreNames.users.name).document(participant)
            participantDocument
                    .update("myCalcRoomIds", FieldValue.arrayUnion(roomId))
                    .addOnCompleteListener {
                        if (it.isSuccessful) continuation.resume(true)
                        else continuation.resume(false)
                    }
        }
    }

    override suspend fun isCompletedStatus(calcRoomId: String) = suspendCoroutine<Boolean> { continuation ->
        firestore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).collection(FireStoreNames.receipts.name).whereEqualTo("status", false)
                .limit(1).get().addOnCompleteListener {
                    if (it.isSuccessful)
                        continuation.resume(it.result.isEmpty)
                    else
                        continuation.resumeWithException(it.exception!!)
                }
    }
}