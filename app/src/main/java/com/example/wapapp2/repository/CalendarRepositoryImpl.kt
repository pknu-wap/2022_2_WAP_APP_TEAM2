package com.example.wapapp2.repository

import android.util.Log
import androidx.core.content.ContextCompat
import com.example.wapapp2.R
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.interfaces.CalendarRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.asTask
import kotlinx.coroutines.tasks.await
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

    /** snapshotlistener 분리 필요 register 변수 가져와야함 **/
    override suspend fun getMyReceipts_with_addSnapshot(myCalcRoomIds: MutableSet<String>, snapshotlistener : EventListener<QuerySnapshot>)
    : Pair<HashMap<String, List<ReceiptDTO>>, List<ListenerRegistration>> {
        val tmpHashMap = HashMap<String, List<ReceiptDTO>>()
        val tmpListRegisteration = listOf<ListenerRegistration>()

        val tasks = listOf<Deferred<Task<Any>>>()
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
                            val dstKey = DateTime.parse(receiptDTO.date).toString("yyyyMMdd")
                            if (tmpHashMap.containsKey(receiptDTO.date)){
                                tmpHashMap[dstKey]!!.plus(receiptDTO)
                            }else {
                                tmpHashMap[dstKey] = listOf(receiptDTO)
                            }
                        }
                    }.await()
            }

        return Pair(tmpHashMap, tmpListRegisteration)
    }

}