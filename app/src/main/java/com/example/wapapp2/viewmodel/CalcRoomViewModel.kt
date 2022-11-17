package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.CalcRoomRepositorylmpl

class CalcRoomViewModel : ViewModel() {
    private val calcRoomRepositoryImpl = CalcRoomRepositorylmpl.getINSTANCE()
    val currentFriendsList: ArrayList<FriendDTO> = DummyData.getMyFriendsList()

    val participants = MutableLiveData<MutableList<UserDTO>>()

    
}