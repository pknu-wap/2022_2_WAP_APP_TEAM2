package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.model.DocumentCollections

interface ReceiptRepository {
    suspend fun addReceipt(receiptDTO: ReceiptDTO, calcRoomId: String): Boolean
    suspend fun addProducts(documentReference: DocumentReference, productsList: ArrayList<ReceiptProductDTO>): Boolean
    suspend fun getLastDocumentReference(calcRoomId: String): DocumentReference?
}