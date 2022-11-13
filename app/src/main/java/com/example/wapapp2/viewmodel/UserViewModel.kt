package com.example.wapapp2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.FriendsRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class UserViewModel : ViewModel() {
    private val friendsRepositoryImpl: FriendsRepositoryImpl = FriendsRepositoryImpl.getINSTANCE()!!
    val myFriendsIdSet = mutableSetOf<String>()
    val searchUsersResult = MutableLiveData<MutableList<UserDTO>>()

    fun findUsers(email: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.findUsers(email)
            }
            val dtoSet = result.await()
            for (v in dtoSet) {
                if (myFriendsIdSet.contains(v.id)) {
                    dtoSet.remove(v)
                }
            }
            withContext(Dispatchers.Main) {
                searchUsersResult.value = dtoSet.toMutableList()
            }
        }
    }
}