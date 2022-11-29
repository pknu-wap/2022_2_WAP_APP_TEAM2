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
    override fun addSnapShotListner(myCalcRoomID: String, eventListener: EventListener<QuerySnapshot>) :ListenerRegistration =
        firestore.collection(FireStoreNames.calc_rooms.name)
            .document(myCalcRoomID)
            .collection(FireStoreNames.receipts.name).addSnapshotListener(eventListener)


}