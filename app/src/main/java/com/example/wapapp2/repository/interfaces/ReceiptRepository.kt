package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.model.ReceiptProductParticipantDTO
import com.google.firebase.firestore.*
import com.google.firebase.firestore.model.DocumentCollections

interface ReceiptRepository {
    suspend fun addReceipt(receiptDTO: ReceiptDTO, calcRoomId: String): Boolean
    suspend fun addProducts(receiptId: String, productsList: MutableList<ReceiptProductDTO>, calcRoomId: String): Boolean
    suspend fun getLastDocumentId(calcRoomId: String): String?
    suspend fun modifyReceipt(map: MutableMap<String, Any?>, calcRoomId: String, receiptId: String): Boolean
    suspend fun removeReceipt(calcRoomId: String, receiptId: String)
    suspend fun removeProducts(calcRoomId: String, receiptId: String, removeIds: MutableList<String>): Boolean
    suspend fun modifyProducts(productMap: MutableMap<String, ReceiptProductDTO>, calcRoomId: String, receiptId: String): Boolean
    suspend fun getReceipts(calcRoomId: String): MutableList<ReceiptDTO>
    suspend fun getReceipts(calcRoomId: String, receiptIds: List<String>): MutableList<ReceiptDTO>
    fun snapshotReceipts(calcRoomId: String, eventListener: EventListener<QuerySnapshot>): ListenerRegistration
    fun snapshotReceipt(calcRoomId: String, receiptId: String, eventListener: EventListener<DocumentSnapshot>): ListenerRegistration
    suspend fun getProducts(receiptId: String, calcRoomId: String): MutableList<ReceiptProductDTO>
    suspend fun addOngoingReceipt(receiptId: String, calcRoomId: String): Boolean
    suspend fun updateMyIdInCheckedParticipants(add: Boolean, calcRoomId: String, receiptId: String)

    suspend fun updateMyIdFromProductParticipantIds(
            add: Boolean, calcRoomId: String, receiptId: String, productId: String,
            participantDTO: ReceiptProductParticipantDTO,
    )
}