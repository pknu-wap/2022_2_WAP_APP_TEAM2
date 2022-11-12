package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.firebase.ui.firestore.FirestoreRecyclerOptions

interface ChatRepository {
    suspend fun sendMsg(calcRoomDTO: CalcRoomDTO, chatDTO: ChatDTO)
}