package com.example.wapapp2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.datastore.MyDataStore
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
        CoroutineScope(Dispatchers.IO).launch {
            val result = async {
                userRepositoryImpl.getUser(auth.currentUser!!.uid)
            }
            result.await()
            withContext(Main) {
                myProfileData.value = result.await()
            }
        }
    }

    fun checkFcmToken() {
        CoroutineScope(Dispatchers.IO).launch {
            MyDataStore.getINSTANCE().checkFcmToken()
        }
    }

    fun init() {
        initMyProfile()
        checkFcmToken()
    }

    /**
     * 로그인 여부 확인
     * 로그인 된 상태 -> true
     * else -> false
     **/
    fun onSignIn(): Boolean {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null)
            init()

        return auth.currentUser != null
    }

    fun signIn() {

    }
}