package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.repository.interfaces.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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

    override suspend fun sendMsg(roomId: String, chatDTO: ChatDTO) = suspendCoroutine<Boolean> { continuation ->
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

    override suspend fun isEmptyChatList(roomId: String): Boolean = suspendCoroutine<Boolean> { continuation ->
        Firebase.firestore
                .collection(FireStoreNames.calc_rooms.name)
                .document(roomId)
                .collection(FireStoreNames.chats.name)
                .limit(1)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        continuation.resume(it.result.isEmpty)
                    else
                        continuation.resumeWithException(it.exception!!)
                }
    }
}