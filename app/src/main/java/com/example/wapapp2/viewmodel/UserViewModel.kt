package com.example.wapapp2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.UserRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class UserViewModel : ViewModel() {
    private val userRepositoryImpl: UserRepositoryImpl = UserRepositoryImpl.getINSTANCE()!!
    val myFriendsIdSet = mutableSetOf<String>()
    val searchUsersResult = MutableLiveData<MutableList<UserDTO>>()
    val user = MutableLiveData<UserDTO?>()
    val users = MutableLiveData<MutableList<UserDTO>>()

    fun findUsers(email: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                userRepositoryImpl.findUsers(email)
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

    fun getUser(userId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                userRepositoryImpl.getUser(userId)
            }
            result.await()
            withContext(Main) {
                user.value = result.await()
            }
        }
    }

    fun getUsers(userIds: MutableList<String>) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                userRepositoryImpl.getUsers(userIds)
            }
            result.await()
            withContext(Main) {
                users.value = result.await()
            }
        }
    }
}