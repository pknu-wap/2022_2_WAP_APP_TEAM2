package com.example.wapapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.CalenderRepositoryImpl
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.*

class CalenderViewModel : ViewModel() {
    private val myCalenderRepositoryImpl = CalenderRepositoryImpl.getINSTANCE()
    val myReceiptDTOs = MutableLiveData<MutableSet<ReceiptDTO>>()
    private var myCalcRoomIdsListener: ListenerRegistration? = null

    fun getCalenderReceipts(myCalcRooms : LiveData<Set<CalcRoomDTO>>){
        myCalcRooms.value?.let{
            for(myCalcRoom in it)
                CoroutineScope(Dispatchers.Default).launch {
                    val tmpReceiptDTOs =  async { myCalenderRepositoryImpl.loadMyReceipts(myCalcRoom) }
                    withContext(MainScope().coroutineContext){
                        myReceiptDTOs.value!!.addAll(tmpReceiptDTOs.await())
                    }
                }
        }
    }

}