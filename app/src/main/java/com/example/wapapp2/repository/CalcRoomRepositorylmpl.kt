package com.example.wapapp2.repository

import android.provider.SyncStateContract.Helpers.update
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.repository.interfaces.CalcRoomRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
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
        val myRoomIDsDocument = firestore.collection(FireStoreNames.users.name).document(auth.currentUser!!.uid)
        newDocument.set(calcRoomDTO).addOnCompleteListener {
            if (it.isSuccessful){
                calcRoomDTO.id = newDocument.id // 참여 정산방목록으로 저장
                myRoomIDsDocument
                    .update("myCalcRoomIds", FieldValue.arrayUnion(calcRoomDTO.id))
                    .addOnCompleteListener {
                        if(it.isSuccessful) continuation.resume(true)
                        else continuation.resume(false)
                    }
            }
            else
                continuation.resume(false)
        }
    }

    /**
     * 정산방 삭제
     */
    override suspend fun deleteCalcRoom(calcRoomDTO: CalcRoomDTO) {
        firestore
                .collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomDTO.id.toString()).delete()
                .addOnCompleteListener { task ->
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
     * 정산방에서 나가기
     */
    override suspend fun exitFromCalcRoom(roomId: String) = suspendCoroutine<Boolean> { continuation ->
        val myId = auth.currentUser!!.uid
        firestore.collection(FireStoreNames.calc_rooms.name)
                .document(roomId).update("participantIds", FieldValue.arrayRemove(myId))
                .addOnCompleteListener {
                    continuation.resume(it.isSuccessful)
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
}