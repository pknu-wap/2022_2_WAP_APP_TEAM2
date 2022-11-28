package com.example.wapapp2.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.datastore.MyDataStore
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.UserImgRepositoryImpl
import com.example.wapapp2.repository.UserRepositoryImpl
import com.example.wapapp2.repository.interfaces.UserImgRepository
import com.example.wapapp2.repository.interfaces.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main

class MyAccountViewModel : ViewModel() {
    private val userRepository : UserRepository = UserRepositoryImpl.getINSTANCE()
    private val userImgRepository : UserImgRepository = UserImgRepositoryImpl.getINSTANCE()

    private val auth = FirebaseAuth.getInstance()
    val myProfileData = MutableLiveData<UserDTO>()

    fun initMyProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = async {
                userRepository.getUser(auth.currentUser!!.uid)
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



    //있으면 지우고 해야함
    fun SetMyProfileUri(uri : Uri){
        CoroutineScope(Default).launch {
            if(myProfileData.value!!.imgUri.isEmpty().not())
                async { userImgRepository.deleteProfileUri(myProfileData.value!!.imgUri)}

            val url = async { userImgRepository.uploadProfileUri(myProfileData.value!!.id,uri)}
            url.await()?.apply {
                val resultCode = async { userRepository.setMyProfileUrl(this@apply) }
                resultCode.await()?.let{
                    withContext(Main){
                        val tmp = myProfileData.value!!
                        tmp.imgUri = this@apply
                        myProfileData.value = tmp
                    }
                }
            }
        }
    }

    fun DeleteMyProfileUrl(){
        CoroutineScope(Default).launch {
            val result = async { userImgRepository.deleteProfileUri(myProfileData.value!!.imgUri)}
            result.await()?.apply {
                if(this){
                    val resultCode = async { userRepository.setMyProfileUrl("") }
                    resultCode.await()?.let{
                        withContext(Main){
                            val tmp = myProfileData.value!!
                            tmp.imgUri = ""
                            myProfileData.value = tmp
                        }
                    }
                }
            }
        }
    }
}