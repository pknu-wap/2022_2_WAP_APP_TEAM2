package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.FriendDTO
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

interface CalcRoomRepository {
    suspend fun addNewCalcRoom(calcRoomDTO: CalcRoomDTO) : Boolean
    suspend fun getCalcRoom(roomId: String): CalcRoomDTO
    suspend fun inviteFriends(list: MutableList<FriendDTO>, roomId: String)
    fun getMyCalcRoomIds(listener: EventListener<DocumentSnapshot>): ListenerRegistration
    suspend fun exitFromCalcRoom(roomId: String): Boolean
    fun snapshotCalcRoom(roomId: String, listener: EventListener<DocumentSnapshot>): ListenerRegistration
}