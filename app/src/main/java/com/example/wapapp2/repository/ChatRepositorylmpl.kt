package com.example.wapapp2.repository

import android.widget.Toast
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.repository.interfaces.ChatRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.HashMap

class ChatRepositorylmpl private constructor() : ChatRepository {
    private val fireStore = FirebaseFirestore.getInstance()


    companion object {
        private lateinit var INSTANCE: ChatRepositorylmpl

        fun getINSTANCE() = INSTANCE

        fun initialize() {
            INSTANCE = ChatRepositorylmpl()

        }
    }

    override suspend fun sendMsg(calcRoomDTO: CalcRoomDTO, chatDTO: ChatDTO) {
        fireStore
            .collection("calc_rooms")
            .document(calcRoomDTO.id!!)
            .collection("chats")
            .document()
            .set(chatDTO)
            .addOnFailureListener { exception ->
                TODO("전송실패")
            }
    }
}