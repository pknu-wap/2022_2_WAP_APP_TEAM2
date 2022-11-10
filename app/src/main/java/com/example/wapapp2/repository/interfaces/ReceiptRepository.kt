package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.model.DocumentCollections

interface ReceiptRepository {
    suspend fun addReceipt(receiptDTO: ReceiptDTO, calcRoomId: String): Boolean
    suspend fun addProducts(documentId: String, productsList: ArrayList<ReceiptProductDTO>, calcRoomId: String): Boolean
    suspend fun getLastDocumentId(calcRoomId: String): String?
    suspend fun modifyReceipt(map: HashMap<String, Any?>, calcRoomId: String): Boolean
    suspend fun deleteReceipt(calcRoomId: String, receiptId: String): Boolean
    suspend fun modifyProducts(productMapList: ArrayList<HashMap<String, Any?>>, calcRoomId: String): Boolean
    suspend fun getReceipts(calcRoomId: String): MutableList<ReceiptDTO>
    suspend fun getProducts(receiptId: String, calcRoomId: String): MutableList<ReceiptProductDTO>

    fun addMyID_fromProductParticipantIDs(product_id: String)
    fun subMyID_fromProductParticipantIDs(product_id: String)
}