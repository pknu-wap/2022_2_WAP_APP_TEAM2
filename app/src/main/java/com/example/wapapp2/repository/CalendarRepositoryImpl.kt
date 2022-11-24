package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.interfaces.CalendarRepository
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import org.joda.time.DateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CalendarRepositoryImpl private constructor() : CalendarRepository{
    private val firestore  = FirebaseFirestore.getInstance()

    companion object {
        private lateinit var INSTANCE: CalendarRepositoryImpl

        fun getINSTANCE() = INSTANCE

        fun initialize() {
            INSTANCE = CalendarRepositoryImpl()
        }
    }

    /** 각 방에 대해 수행 **/
    override suspend fun getMyReceipts_with_addSnapshot(myCalcRoomID: String, snapshotlistener : EventListener<QuerySnapshot>)
    = suspendCoroutine <Pair<HashMap<String, ArrayList<ReceiptDTO>>, ArrayList<ListenerRegistration>>> { continuation ->
        val tmpHashMap = HashMap<String, ArrayList<ReceiptDTO>>()
        val tmpListRegisteration = ArrayList<ListenerRegistration>()
        val receiptCollection = firestore.collection(FireStoreNames.calc_rooms.name)
                .document(myCalcRoomID)
                .collection(FireStoreNames.receipts.name)

        // snapshot 연결
        tmpListRegisteration.plus(receiptCollection.addSnapshotListener(snapshotlistener))

        //receipt hashmap 연결
        receiptCollection.get()
            .addOnSuccessListener {
                for (dc in it.documents.toMutableList()) {
                    val receiptDTO = dc.toObject<ReceiptDTO>()!!
                    receiptDTO.roomID = myCalcRoomID
                    val dstKey = DateTime.parse(receiptDTO.date.toString()).toString("yyyyMMdd")
                    if (tmpHashMap.containsKey(dstKey)) {
                        tmpHashMap[dstKey]!!.add(receiptDTO)
                    } else {
                        tmpHashMap[dstKey] = arrayListOf(receiptDTO)
                    }
                }
                continuation.resume(Pair( tmpHashMap ,tmpListRegisteration))
            }
        }

}