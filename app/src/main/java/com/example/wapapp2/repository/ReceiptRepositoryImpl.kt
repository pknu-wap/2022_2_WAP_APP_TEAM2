package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.interfaces.ReceiptRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReceiptRepositoryImpl private constructor() : ReceiptRepository {
    private val fireStore = FirebaseFirestore.getInstance()

    companion object {
        private var INST: ReceiptRepositoryImpl? = null
        val INSTANCE get() = INST!!

        fun initialize() {
            INST = ReceiptRepositoryImpl()
        }

    }


    override suspend fun addReceipt(receiptDTO: ReceiptDTO, calcRoomId: String): Boolean {
        val receiptCollection = fireStore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).collection(FireStoreNames.receipts.name)
        var result = false
        receiptCollection.document().set(receiptDTO).addOnSuccessListener { it1 ->
            //영수증 추가 완료
            //영수증 항목 추가작업(추가된 영수증의 document를 가져옴)
            result = true
        }.addOnFailureListener { e ->

        }.await()

        return result
    }

    override suspend fun addProducts(documentReference: DocumentReference, productsList: ArrayList<ReceiptProductDTO>
    ): Boolean {
        var result = false
        fireStore.runBatch { batch ->
            for (receiptProduct in productsList) {
                batch.set(documentReference
                        .collection("products").document(),
                        receiptProduct)
            }
        }.addOnSuccessListener {
            //영수증 항목 추가 완료
            //모든 작업 종료됨
            result = true
        }.addOnFailureListener { e ->

        }.await()
        return result
    }

    override suspend fun getLastDocumentReference(calcRoomId: String): DocumentReference? {
        val receiptCollection = fireStore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).collection(FireStoreNames.receipts.name)
        var documentReference: DocumentReference? = null

        receiptCollection.orderBy("createdBy", Query.Direction.ASCENDING).limit(1)
                .get().addOnSuccessListener { it2 ->
                    documentReference = it2.documents[0].reference
                }.addOnFailureListener { e ->
                }.await()

        return documentReference
    }
}