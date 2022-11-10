package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.interfaces.ReceiptRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReceiptRepositoryImpl private constructor() : ReceiptRepository {
    private val fireStore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    companion object {
        private var INST: ReceiptRepositoryImpl? = null
        val INSTANCE get() = INST!!

        fun initialize() {
            INST = ReceiptRepositoryImpl()
        }

    }

    /** receipt_product 컬렉션의 참여 유저 id에서 자기 아이디 추가
     * when : checked
     * **/
    override fun addMyID_fromProductParticipantIDs(product_id: String) {

    }

    /** receipt_product 컬렉션의 참여 유저 id에서 자기 아이디 제외
     * when : unchecked
     * **/
    override fun subMyID_fromProductParticipantIDs(product_id: String) {

    }


    override suspend fun addReceipt(receiptDTO: ReceiptDTO, calcRoomId: String) = suspendCoroutine<Boolean> { continuation ->
        val receiptCollection = fireStore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).collection(FireStoreNames.receipts.name)
        receiptDTO.payersId = currentUser!!.uid

        receiptCollection.document().set(receiptDTO).addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
    }

    override suspend fun addProducts(documentId: String, productsList: ArrayList<ReceiptProductDTO>, calcRoomId: String
    ) = suspendCoroutine<Boolean> { continuation ->
        val writeBatch = fireStore.batch()

        for (receiptProduct in productsList) {
            writeBatch.set(fireStore.collection(FireStoreNames.calc_rooms.name)
                    .document(calcRoomId).collection(FireStoreNames.receipts.name)
                    .document(documentId).collection(FireStoreNames.products.name).document(), receiptProduct)
        }

        writeBatch.commit().addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }

    }

    override suspend fun getLastDocumentId(calcRoomId: String) = suspendCoroutine<String?> { continuation ->
        val receiptCollection = fireStore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).collection(FireStoreNames.receipts.name)

        receiptCollection.orderBy("createdTime", Query.Direction.DESCENDING).limit(1)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(it.result.documents[0].id)
                    } else {
                        continuation.resume(null)
                    }
                }
    }

    override suspend fun modifyReceipt(map: HashMap<String, Any?>, calcRoomId: String) = suspendCoroutine<Boolean> { continuation ->
        val receiptCollection = fireStore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).collection(FireStoreNames.receipts.name)
        receiptCollection.document().update(map).addOnCompleteListener { continuation.resume(it.isSuccessful) }
    }

    override suspend fun deleteReceipt(calcRoomId: String, receiptId: String) = suspendCoroutine<Boolean> { continuation ->
        val receiptDocument = fireStore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).collection(FireStoreNames.receipts.name).document(receiptId)

        receiptDocument.delete().addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
    }

    override suspend fun modifyProducts(productMapList: ArrayList<HashMap<String, Any?>>,
                                        calcRoomId: String) = suspendCoroutine<Boolean> { continuation ->
        val collection = fireStore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).collection(FireStoreNames.receipts.name)

        fireStore.runBatch { batch ->
            for (map in productMapList) {
                val id = map["id"].toString()
                map.remove("id")

                batch.update(collection.document(id), map)
            }
        }.addOnCompleteListener { continuation.resume(it.isSuccessful) }
    }
}