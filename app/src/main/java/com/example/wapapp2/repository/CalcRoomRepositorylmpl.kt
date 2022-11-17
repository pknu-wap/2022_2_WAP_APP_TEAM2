package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.repository.interfaces.CalcRoomRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CalcRoomRepositorylmpl private constructor() : CalcRoomRepository {
    val firestore = FirebaseFirestore.getInstance()

    companion object {
        private lateinit var INSTANCE: CalcRoomRepositorylmpl

        fun getINSTANCE() = INSTANCE

        fun initialize() {
            INSTANCE = CalcRoomRepositorylmpl()
        }
    }

    override suspend fun addNewCalcRoom(calcRoomDTO: CalcRoomDTO) {
        val newDocument = firestore
                .collection(FireStoreNames.calc_rooms.name)
                .document()
        newDocument.set(calcRoomDTO).addOnSuccessListener {
            newDocument.id // 참여 정산방목록으로 저장
        }

    }

    override suspend fun deleteCalcRoom(calcRoomDTO: CalcRoomDTO) {
        firestore
                .collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomDTO.id.toString()).delete()
                .addOnSuccessListener { }
    }

    fun snapshotCalcRoom(
            roomId: String,
            listener: EventListener<DocumentSnapshot>,
    ): ListenerRegistration = firestore.collection(FireStoreNames.calc_rooms
            .name).document(roomId).addSnapshotListener(listener)

}