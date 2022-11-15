package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.repository.interfaces.MyCalcRoomRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MyCalcRoomRepositoryImpl : MyCalcRoomRepository {
    private val firebase = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private var INSTANCE: MyCalcRoomRepositoryImpl? = null

        fun initialize() {
            INSTANCE = MyCalcRoomRepositoryImpl()
        }

        fun getINSTANCE() = INSTANCE!!
    }

    override suspend fun getMyCalcRooms(): MutableMap<String, CalcRoomDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun exitFromCalcRoom(roomId: String) = suspendCoroutine<Boolean> { continuation ->
        val myId = auth.currentUser!!.uid
        firebase.collection(FireStoreNames.calc_rooms.name)
                .document(roomId).update("participants", FieldValue.arrayRemove(myId))
                .addOnCompleteListener {
                    continuation.resume(it.isSuccessful)
                }
    }

    override fun getMyCalcRoomIds(snapshotListener: EventListener<DocumentSnapshot>): ListenerRegistration =
            firebase.collection(FireStoreNames.users.name).document(auth.currentUser!!.uid)
                    .addSnapshotListener(snapshotListener)


}