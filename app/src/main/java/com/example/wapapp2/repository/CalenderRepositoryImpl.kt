package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.interfaces.CalenderRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CalenderRepositoryImpl private constructor() : CalenderRepository{
    private val firestore  = FirebaseFirestore.getInstance()

    companion object {
        private lateinit var INSTANCE: CalenderRepositoryImpl

        fun getINSTANCE() = INSTANCE

        fun initialize() {
            INSTANCE = CalenderRepositoryImpl()
        }
    }

    override suspend fun loadMyReceipts(myCalcRoom: CalcRoomDTO) = suspendCoroutine<MutableSet<ReceiptDTO>> {continuation ->
        val result = mutableSetOf<ReceiptDTO>()
        firestore.collection(FireStoreNames.calc_rooms.name)
            .document(myCalcRoom.id.toString())
            .collection(FireStoreNames.receipts.name).get()
            .addOnSuccessListener {
                for (receiptDTO in it.documents.toMutableList()) {
                    result.add(receiptDTO.toObject<ReceiptDTO>()!!)
                }
                continuation.resume(result)
            }.addOnFailureListener {
                continuation.resume(result)
            }
    }
}