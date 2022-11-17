package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.CalcRoomDTO
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

interface CalcRoomRepository {
    suspend fun addNewCalcRoom(calcRoomDTO: CalcRoomDTO)
    suspend fun deleteCalcRoom(calcRoomDTO: CalcRoomDTO)
    fun snapshotCalcRoom(roomId: String, listener: EventListener<DocumentSnapshot>): ListenerRegistration
}