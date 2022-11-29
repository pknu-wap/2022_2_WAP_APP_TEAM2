package com.example.wapapp2.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.CalendarRepositoryImpl
import com.example.wapapp2.repository.interfaces.CalendarRepository
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import org.joda.time.DateTime

class MyCalendarViewModel : ViewModel() {
    private val myCalendarRepository: CalendarRepository = CalendarRepositoryImpl.getINSTANCE()
    private val myCalcRoomReceiptListeners: ArrayList<ListenerRegistration> = arrayListOf()

    /** Hashmap of my ReceiptDTOs <DateString ISO8610, ReceiptDTO> **/
    var myReceiptMapLivedata = MutableLiveData<HashMap<String, ArrayList<ReceiptDTO>>>(hashMapOf())
    val myReceiptMap get() = myReceiptMapLivedata.value

    fun loadCalendarReceipts(myCalcRoomIDs: MutableSet<String>) {
        for (myCalcRoomID in myCalcRoomIDs) {
            myCalcRoomReceiptListeners.add(myCalendarRepository.addSnapShotListner(
                myCalcRoomID,
                EventListener { value, error ->
                    value?.apply {
                        val tmp = hashMapOf<String, ArrayList<ReceiptDTO>>()
                        for(dc in this){
                            val newReceiptDTO = dc.toObject(ReceiptDTO::class.java)
                            newReceiptDTO.roomID = myCalcRoomID
                            val keyDateString = DateTime.parse(newReceiptDTO.date.toString()).toString("yyyyMMdd")
                            if (tmp.containsKey(keyDateString))
                                tmp[keyDateString]!!.add(newReceiptDTO)
                            else
                                tmp[keyDateString] = arrayListOf(newReceiptDTO)
                        }
                        myReceiptMapLivedata.value = tmp
                    }
                }))
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRemove()
    }

    fun listenerRemove(){
        if (myCalcRoomReceiptListeners.isEmpty().not()) {
            for (register in myCalcRoomReceiptListeners) {
                register.remove()
            }
        }
    }

}