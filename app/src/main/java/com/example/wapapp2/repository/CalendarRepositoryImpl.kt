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
import org.checkerframework.framework.qual.DefaultFor
import org.joda.time.DateTime
import kotlin.coroutines.coroutineContext
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
                    val dstKey = DateTime.parse(receiptDTO.date.toString()).toString("yyyyMMdd")
                    if (tmpHashMap.containsKey(dstKey)) {
                        tmpHashMap[dstKey]!!.add(receiptDTO)
                        Log.d("기존에 있던 것에 추가됨", receiptDTO.toString())
                        Log.d("추가후",tmpHashMap.toString())
                    } else {
                        tmpHashMap[dstKey] = arrayListOf(receiptDTO)
                        Log.d("새로 추가됨", receiptDTO.toString())
                    }
                }
                Log.d("returned tmpHashmap", tmpHashMap.toString())
                continuation.resume(Pair( tmpHashMap ,tmpListRegisteration))
            }
        }

}