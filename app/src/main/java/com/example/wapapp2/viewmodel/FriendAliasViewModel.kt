package com.example.wapapp2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.repository.FriendsLocalRepositoryImpl
import com.example.wapapp2.repository.FriendsRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendAliasViewModel : ViewModel() {
    private val friendsRepositoryImpl: FriendsRepositoryImpl = FriendsRepositoryImpl.getINSTANCE()
    private val friendsLocalRepository = FriendsLocalRepositoryImpl.getINSTANCE()
    var friendDTO: FriendDTO? = null

    fun setAliasToMyFriend(alias: String, friendId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            //서버에 저장
            friendsRepositoryImpl.setAliasToMyFriend(alias, friendId)
            //로컬에 저장
            friendsLocalRepository.update(FriendDTO(friendId, alias))
        }
    }
}