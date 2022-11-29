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
import java.sql.Date
import java.text.DateFormat
import java.text.SimpleDateFormat

class MyCalendarViewModel : ViewModel() {
    private val myCalendarRepository: CalendarRepository = CalendarRepositoryImpl.getINSTANCE()
    private val myCalcRoomReceiptListeners: ArrayList<ListenerRegistration> = arrayListOf()

    /** Hashmap of my ReceiptDTOs <DateString yyyyMMdd, ReceiptDTO> **/
    var myReceiptMapLivedata = MutableLiveData<HashMap<String, ArrayList<ReceiptDTO>>>(hashMapOf())
    val myReceiptMap get() = myReceiptMapLivedata.value

    /** Update될떄마다 값이 바뀜 **/
    var updatedBooleanLiveData = MutableLiveData<Boolean>(true)

    /** 방마다 hashmap 값을 저장 **/
    var myReceiptCalendarMaps = HashMap<String, HashMap<String, ArrayList<ReceiptDTO>>>(hashMapOf())

    fun loadCalendarReceipts(myCalcRoomIDs: MutableSet<String>) {
        for (myCalcRoomID in myCalcRoomIDs) {
            myCalcRoomReceiptListeners.add(
                myCalendarRepository.addSnapShotListner(
                    myCalcRoomID,
                    EventListener { value, error ->
                        val hashMap = hashMapOf<String, ArrayList<ReceiptDTO>>()
                        if (value != null) {
                            for (dc in value) {
                                val newReceiptDTO = dc.toObject(ReceiptDTO::class.java)
                                newReceiptDTO.roomID = myCalcRoomID
                                if (hashMap[SimpleDateFormat("yyyyMMdd").format(newReceiptDTO.createdTime)] == null)
                                    hashMap[SimpleDateFormat("yyyyMMdd").format(newReceiptDTO.createdTime)] = arrayListOf(newReceiptDTO)
                                else
                                    hashMap[SimpleDateFormat("yyyyMMdd").format(newReceiptDTO.createdTime)]!!.add(newReceiptDTO)
                            }
                            myReceiptCalendarMaps[myCalcRoomID] = hashMap
                            updatedBooleanLiveData.value = updatedBooleanLiveData.value!!.not()
                        }
                    }
                )
            )
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

    fun getTotalHashMap() : HashMap<String, ArrayList<ReceiptDTO>>{
        val result = hashMapOf<String, ArrayList<ReceiptDTO>>()
        for ( mapForOneCalcRoom in myReceiptCalendarMaps.values)
            result.putAll(mapForOneCalcRoom)
        return result
    }

}