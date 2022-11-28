package com.example.wapapp2.viewmodel

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
    var myReceiptMapLivedata = MutableLiveData<HashMap<String, ArrayList<ReceiptDTO>>>()
    val myReceiptMap get() = myReceiptMapLivedata.value

    fun loadCalendarReceipts(myCalcRoomIDs: MutableSet<String>) {
        for (myCalcRoomID in myCalcRoomIDs) {
            myCalcRoomReceiptListeners.add(myCalendarRepository.addSnapShotListner(
                myCalcRoomID,
                EventListener { value, error ->
                    value?.documentChanges?.apply {
                        for (dc in this) {
                            if (dc.type == DocumentChange.Type.ADDED){
                                val newReceiptDTO = dc.document.toObject(ReceiptDTO::class.java)
                                newReceiptDTO.roomID = myCalcRoomID
                                ReceiptAdded(newReceiptDTO)
                            } else if (dc.type == DocumentChange.Type.REMOVED)
                                ReceiptRemoved(dc.document.toObject(ReceiptDTO::class.java))
                            else if (dc.type == DocumentChange.Type.MODIFIED)
                                ReceiptModified(dc.document.toObject(ReceiptDTO::class.java))
                        }
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

    fun ReceiptAdded(newReceiptDTO: ReceiptDTO) {
        val keyDateString = DateTime.parse(newReceiptDTO.date.toString()).toString("yyyyMMdd")
        var tmplist = myReceiptMapLivedata.value ?: hashMapOf()

        if (tmplist.containsKey(keyDateString)) {
            tmplist[keyDateString]!!.add(newReceiptDTO)
        } else {
            tmplist[keyDateString] = arrayListOf(newReceiptDTO)
        }

        myReceiptMapLivedata.value = tmplist
    }

    fun ReceiptRemoved(removedReceiptDTO: ReceiptDTO) {
        val keyDateString = DateTime.parse(removedReceiptDTO.date.toString()).toString("yyyyMMdd")

        var tmplist = myReceiptMapLivedata.value ?: hashMapOf()
        tmplist[keyDateString]?.let {
            it.remove(removedReceiptDTO)
        }
        myReceiptMapLivedata.value = tmplist
    }

    fun ReceiptModified(modifiedReceiptDTO: ReceiptDTO){
        val keyDateString = DateTime.parse(modifiedReceiptDTO.date.toString()).toString("yyyyMMdd")

        var tmplist = myReceiptMapLivedata.value ?: hashMapOf()
        tmplist[keyDateString]?.let {
            it.remove( it.find { it.id == modifiedReceiptDTO.id } )
            it.add(modifiedReceiptDTO)
        }
        myReceiptMapLivedata.value = tmplist
    }
}