package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.google.firebase.firestore.*
import com.google.firebase.firestore.model.DocumentCollections

interface ReceiptRepository {
    suspend fun addReceipt(receiptDTO: ReceiptDTO, calcRoomId: String): Boolean
    suspend fun addProducts(receiptId: String, productsList: MutableList<ReceiptProductDTO>, calcRoomId: String): Boolean
    suspend fun getLastDocumentId(calcRoomId: String): String?
    suspend fun modifyReceipt(map: MutableMap<String, Any?>, calcRoomId: String, receiptId: String): Boolean
    suspend fun removeReceipt(calcRoomId: String, receiptId: String): Boolean
    suspend fun removeProducts(calcRoomId: String, receiptId: String, removeIds: MutableList<String>): Boolean
    suspend fun modifyProducts(productMap: MutableMap<String, ReceiptProductDTO>, calcRoomId: String, receiptId: String): Boolean
    suspend fun getReceipts(calcRoomId: String): MutableList<ReceiptDTO>
    fun snapshotReceipts(calcRoomId: String, eventListener: EventListener<QuerySnapshot>): ListenerRegistration
    suspend fun getProducts(receiptId: String, calcRoomId: String): MutableList<ReceiptProductDTO>

    suspend fun addMyID_fromProductParticipantIDs(product_id: String)
    suspend fun subMyID_fromProductParticipantIDs(product_id: String)
}