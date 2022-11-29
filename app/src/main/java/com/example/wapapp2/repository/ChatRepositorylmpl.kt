package com.example.wapapp2.repository

import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.repository.interfaces.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ChatRepositorylmpl private constructor() : ChatRepository {
    private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    companion object {
        private lateinit var INSTANCE: ChatRepositorylmpl

        fun getINSTANCE() = INSTANCE

        fun initialize() {
            INSTANCE = ChatRepositorylmpl()

        }
    }

    override suspend fun sendMsg(roomId: String, chatDTO: ChatDTO) = suspendCoroutine<Boolean> {  continuation ->
        chatDTO.senderId = auth.currentUser!!.uid
        fireStore
                .collection("calc_rooms")
                .document(roomId)
                .collection("chats")
                .document()
                .set(chatDTO)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resume(false)
                }
    }
}