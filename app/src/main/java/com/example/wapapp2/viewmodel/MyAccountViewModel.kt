package com.example.wapapp2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class MyAccountViewModel : ViewModel() {
    private val userRepositoryImpl = UserRepositoryImpl.getINSTANCE()
    private val auth = FirebaseAuth.getInstance()
    val myProfileData = MutableLiveData<UserDTO>()

    fun initMyProfile() {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                userRepositoryImpl.getUser(auth.currentUser!!.uid)
            }
            result.await()
            withContext(Main) {
                myProfileData.value = result.await()
            }
        }
    }
}