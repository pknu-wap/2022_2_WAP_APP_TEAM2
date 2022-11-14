package com.example.wapapp2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MyAccountViewModel(application: Application) : AndroidViewModel(application) {
    var myName: String
    var myAccountId: String
    val myCalcRooms = MutableLiveData<CalcRoomDTO>()
    val auth: FirebaseAuth

    init {
        auth = FirebaseAuth.getInstance()
        myAccountId = auth.uid.toString()
        Log.d("myAccountId", myAccountId)
        myName = ""
    }

    fun setCurrentUser(userDTO: UserDTO) {
        myName = userDTO.name
        myAccountId = userDTO.id
    }

}