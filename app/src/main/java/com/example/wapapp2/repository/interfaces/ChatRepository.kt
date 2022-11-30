package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

interface ChatRepository {
    suspend fun sendMsg(roomId: String, chatDTO: ChatDTO) : Boolean
    suspend fun isEmptyChatList(roomId: String): Boolean
}