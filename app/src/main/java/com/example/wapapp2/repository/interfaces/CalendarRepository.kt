package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.ReceiptDTO
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

interface CalendarRepository {
    suspend fun getMyReceipts_with_addSnapshot(myCalcRoomIds : MutableSet<String>, eventListener: EventListener<QuerySnapshot>) : Pair<HashMap<String, List<ReceiptDTO>>, List<ListenerRegistration>>
}