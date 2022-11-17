package com.example.wapapp2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.UserDTO
import com.google.firebase.firestore.ktx.toObject

class CalenderViewModel : ViewModel() {

/*
    fun loadMyCalenderRecords() {
        if (myCalcRoomIdsListener == null) {
            myCalcRoomIdsListener = myCalcRoomRepositoryImpl.getMyCalcRoomIds { event, error ->
                val userDTO = event?.toObject<UserDTO>()

                userDTO?.also {
                    val newIdSet = it.myCalcRoomIds.toMutableSet()
                    //id가 수정된 게 있는 지 확인
                    if (newIdSet != myCalcRoomIds.value) {
                        myCalcRoomIds.value = newIdSet
                    }
                }
            }
        }
    }
    */
}