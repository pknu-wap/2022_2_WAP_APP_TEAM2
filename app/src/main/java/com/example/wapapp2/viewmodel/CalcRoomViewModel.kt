package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.FriendDTO

class CalcRoomViewModel(application: Application) : AndroidViewModel(application) {
    val currentFriendsList: ArrayList<FriendDTO> = DummyData.getMyFriendsList()
}