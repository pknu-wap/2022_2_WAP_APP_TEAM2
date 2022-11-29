package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.ReceiptDTO
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

interface CalendarRepository {
    fun addSnapShotListner(myCalcRoomID: String, eventListener: EventListener<QuerySnapshot>) : ListenerRegistration
}