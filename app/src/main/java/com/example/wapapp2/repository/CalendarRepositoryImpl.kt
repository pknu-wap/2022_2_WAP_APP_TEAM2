package com.example.wapapp2.repository

import android.util.Log
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.interfaces.CalendarRepository
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
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

    /** snapshotlistener 분리 필요 register 변수 가져와야함 **/
    override suspend fun getMyReceipts_with_addSnapshot(myCalcRoomIds: MutableSet<String>, snapshotlistener : EventListener<QuerySnapshot>)
    = suspendCoroutine<Pair<HashMap<String, List<ReceiptDTO>>, List<ListenerRegistration>>> { continuation ->
        val tmpHashMap = HashMap<String, List<ReceiptDTO>>()
        val tmpListRegisteration = listOf<ListenerRegistration>()

        //각 방에 대해 수행
        for(myCalcRoomID in myCalcRoomIds){
            val receiptCollection = firestore.collection(FireStoreNames.calc_rooms.name)
                .document(myCalcRoomID)
                .collection(FireStoreNames.receipts.name)

            // snapshot 연결
            tmpListRegisteration.plus(receiptCollection.addSnapshotListener(snapshotlistener))

            //receipt hashmap 연결
            receiptCollection.get()
                .addOnSuccessListener {
                    for (dc in it.documents.toMutableList()) {
                        val receiptDTO =  dc.toObject<ReceiptDTO>()!!
                        Log.d("receiptDTO From Document", receiptDTO.toString())
                        if (tmpHashMap.containsKey(receiptDTO.date)){
                          tmpHashMap[receiptDTO.date]!!.plus(receiptDTO)
                        }else tmpHashMap[receiptDTO.date] = listOf(receiptDTO)
                    }
                }.addOnFailureListener {
                }
        }
        continuation.resume(Pair(tmpHashMap, tmpListRegisteration))

    }

}