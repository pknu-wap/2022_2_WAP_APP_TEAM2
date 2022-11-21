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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class MyCalendarViewModel : ViewModel() {
    private val myCalendarRepositoryImpl = CalendarRepositoryImpl.getINSTANCE()
    private var myCalcRoomReceiptListeners: ArrayList<ListenerRegistration>? = null

    /** Hashmap of my ReceiptDTOs <DateString ISO8610, ReceiptDTO> **/
    var myReceiptMap = MutableLiveData<HashMap<String, ArrayList<ReceiptDTO>>>()

    fun loadCalendarReceipts(myCalcRoomIDs: MutableSet<String>) {
        for (myCalcRoomID in myCalcRoomIDs) {
            Log.d("loadCalendarReceipt Started", myCalcRoomID.toString())
            viewModelScope.launch {
                val pairOf_map_rl = async {
                    myCalendarRepositoryImpl.getMyReceipts_with_addSnapshot(myCalcRoomID, EventListener { value, error ->
                        value?.documentChanges.apply {
                            for (dc in this!!) {
                                if (dc.type == DocumentChange.Type.ADDED) {
                                    receiptAdded(dc.document.toObject(ReceiptDTO::class.java))
                                } else {
                                    receiptRemoved(dc.document.toObject(ReceiptDTO::class.java))
                                }
                            }
                        }
                    })
                }
                pairOf_map_rl.await()?.let {
                    Log.d("returned it.first", it.first.toString())
                    Log.d("전 :: myReceiptMap.value", myReceiptMap.value.toString())

                    myReceiptMap.value = it.first ?: HashMap()
                    myCalcRoomReceiptListeners = it.second
                    Log.d("후 ::myReceiptMap.value", myReceiptMap.value.toString())
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (myCalcRoomReceiptListeners != null) {
            for (register in myCalcRoomReceiptListeners!!) {
                register.remove()
            }
        }
    }


    fun receiptAdded(newReceiptDTO: ReceiptDTO) {
        val keyDateString = DateTime.parse(newReceiptDTO.date.toString()).toString("yyyyMMdd")

        var tmplist = myReceiptMap.value ?: hashMapOf()
        if (tmplist.containsKey(keyDateString)) {
            tmplist[keyDateString]!!.add(newReceiptDTO)
        } else {
            tmplist[keyDateString] = arrayListOf(newReceiptDTO)
        }
        myReceiptMap.value = tmplist
    }

    fun receiptRemoved(removedReceiptDTO: ReceiptDTO) {
        val keyDateString = DateTime.parse(removedReceiptDTO.date.toString()).toString("yyyyMMdd")

        var tmplist = myReceiptMap.value ?: hashMapOf()
        tmplist[keyDateString]?.let {
            it.remove(removedReceiptDTO)
        }
        myReceiptMap.value = tmplist
    }
}