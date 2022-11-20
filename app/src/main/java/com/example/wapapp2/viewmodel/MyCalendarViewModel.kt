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

class MyCalendarViewModel : ViewModel() {
    private val myCalendarRepositoryImpl = CalendarRepositoryImpl.getINSTANCE()
    private var myCalcRoomReceiptListeners: List<ListenerRegistration>? = null

    /** Hashmap of my ReceiptDTOs <DateString ISO8610, ReceiptDTO> **/
    val myReceiptMap = MutableLiveData<HashMap<String, List<ReceiptDTO>>>()

    fun loadCalendarReceipts(myCalcRooms : MutableSet<String>) {
        if (myCalcRooms.isNotEmpty()){

            Log.e("my calc Rooms", myCalcRooms.toString())
            viewModelScope.launch {
                val pairOf_map_rl = async {
                    Log.d("viewModelScope", "Started")
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
                withContext(MainScope().coroutineContext) {
                    myReceiptMap.value = (pairOf_map_rl.await().first)
                    myCalcRoomReceiptListeners = (pairOf_map_rl.await().second)

                    Log.d("viewModelScope", "Ended")
                    Log.e("myReceiptMap", myReceiptMap.value.toString())
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
            if(myReceiptMap.value!!.containsKey(newReceiptDTO.date)){
                myReceiptMap.value!![newReceiptDTO.date]!!.plus(newReceiptDTO)
            }else
                myReceiptMap.value!![newReceiptDTO.date] = listOf(newReceiptDTO)
        }
    }

    fun receiptRemoved(removedReceiptDTO: ReceiptDTO){
        myReceiptMap.value!![removedReceiptDTO.date]?.let{
            it.minus(removedReceiptDTO)
        }
    }
}