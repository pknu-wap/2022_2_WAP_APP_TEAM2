package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.CalcRoomDTO
import com.google.apphosting.datastore.testing.DatastoreTestTrace
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

interface MyCalcRoomRepository {
    suspend fun getMyCalcRooms(): MutableMap<String, CalcRoomDTO>
    fun getMyCalcRoomIds(listener: EventListener<DocumentSnapshot>): ListenerRegistration
    suspend fun exitFromCalcRoom(roomId: String): Boolean
}