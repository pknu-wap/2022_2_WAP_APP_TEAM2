package com.example.wapapp2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.repository.FriendsRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FriendAliasViewModel : ViewModel() {
    private val friendsRepositoryImpl: FriendsRepositoryImpl = FriendsRepositoryImpl.getINSTANCE()!!
    var friendDTO: FriendDTO? = null

    fun setAliasToMyFriend(alias: String, friendId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.setAliasToMyFriend(alias, friendId)
            }
            result.await()
        }
    }
}