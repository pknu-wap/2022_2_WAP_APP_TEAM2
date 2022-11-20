package com.example.wapapp2.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.CalendarRepositoryImpl
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class MyCalendarViewModel : ViewModel() {
    private val myCalendarRepositoryImpl = CalendarRepositoryImpl.getINSTANCE()
    private var myCalcRoomReceiptListeners: List<ListenerRegistration>? = null

    /** Hashmap of my ReceiptDTOs <DateString ISO8610, ReceiptDTO> **/
    val myReceiptMap = MutableLiveData<HashMap<String, List<ReceiptDTO>>>()

    fun loadCalendarReceipts(myCalcRooms : MutableSet<String>) {
        if (myCalcRooms.isNotEmpty()){

            viewModelScope.launch {
                val pairOf_map_rl = async {
                    myCalendarRepositoryImpl.getMyReceipts_with_addSnapshot(myCalcRooms, EventListener { value, error ->
                        value?.documentChanges.apply {
                            for ( dc in this!!){
                                if(dc.type == DocumentChange.Type.ADDED){
                                    receiptAdded(dc.document.toObject(ReceiptDTO::class.java))
                                }else{
                                    receiptRemoved(dc.document.toObject(ReceiptDTO::class.java))
                                }
                            }
                        }
                    })
                }
                pairOf_map_rl.await()?.let {
                    myReceiptMap.value = (it.first)
                    myCalcRoomReceiptListeners = (it.second)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (myCalcRoomReceiptListeners != null){
            for (register in myCalcRoomReceiptListeners!!) {
                register.remove()
            }
        }
    }


    fun receiptAdded(newReceiptDTO: ReceiptDTO){
        if (myReceiptMap.value!= null){
            val keyDateString = DateTime.parse(newReceiptDTO.date).toString("yyyyMMdd")

            if(myReceiptMap.value!!.containsKey(newReceiptDTO.date)){
                myReceiptMap.value!![keyDateString]!!.plus(newReceiptDTO)
            }else
                myReceiptMap.value!![keyDateString] = listOf(newReceiptDTO)
        }
    }

    fun receiptRemoved(removedReceiptDTO: ReceiptDTO){
        val keyDateString = DateTime.parse(removedReceiptDTO.date).toString("yyyyMMdd")
        myReceiptMap.value!![keyDateString]?.let{
            it.minus(removedReceiptDTO)
        }
    }
}