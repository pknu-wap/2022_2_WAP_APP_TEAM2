package com.example.wapapp2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.FriendsRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class FriendsViewModel : ViewModel() {
    private val friendsRepositoryImpl: FriendsRepositoryImpl = FriendsRepositoryImpl.getINSTANCE()!!

    val friendsListLiveData: MutableLiveData<ArrayList<FriendDTO>> = MutableLiveData<ArrayList<FriendDTO>>(ArrayList<FriendDTO>())
    val friendCheckedLiveData: MutableLiveData<FriendCheckDTO> = MutableLiveData<FriendCheckDTO>()
    val searchResultFriendsLiveData: LiveData<ArrayList<FriendDTO>> = friendsRepositoryImpl.searchResultFriendsLiveData
    val currentRoomFriendsSet = HashSet<String>()

    private val _searchUsersResult = MutableLiveData<MutableList<UserDTO>>()
    val searchUsersResult get() = _searchUsersResult

    private val _myFriends = MutableLiveData<MutableList<FriendDTO>>()
    val myFriends get() = _myFriends

    val myFriendsIdSet = hashSetOf<String>()

    private val _addMyFriendResult = MutableLiveData<Boolean>()
    val addMyFriendResult get() = _addMyFriendResult

    private val _deleteMyFriendResult = MutableLiveData<Boolean>()
    val deleteMyFriendResult get() = _deleteMyFriendResult

    private val _aliasMyFriendResult = MutableLiveData<Boolean>()
    val aliasMyFriendResult get() = _aliasMyFriendResult

    fun checkedFriend(friendDTO: FriendDTO, isChecked: Boolean) {
        val list = friendsListLiveData.value!!
        var contains = false
        var idx = 0
        for ((index, value) in list.withIndex()) {
            if (value.friendUserId == friendDTO.friendUserId) {
                idx = index
                contains = true
                break
            }
        }

        if (isChecked) {
            if (!contains) {
                list.add(friendDTO)
                friendsListLiveData.value = list
                friendCheckedLiveData.value = FriendCheckDTO(isChecked, friendDTO)
            }
        } else
            if (contains) {
                list.removeAt(idx)
                friendsListLiveData.value = list
                friendCheckedLiveData.value = FriendCheckDTO(isChecked, friendDTO)
            }
    }

    fun getFriends(word: String) {
        friendsRepositoryImpl.getFriendsList(word)
    }

    fun getMyFriends() {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.getMyFriends()
            }
            result.await()
            withContext(Main) {
                myFriends.value = result.await()
            }
        }
    }

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
            withContext(Main) {
                searchUsersResult.value = dtoSet.toMutableList()
            }
        }
    }

    fun addToMyFriend(userDTO: UserDTO, myUid: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.addToMyFriend(userDTO, myUid)
            }
            result.await()
            withContext(MainScope().coroutineContext) {
                addMyFriendResult.value = result.await()
            }
        }
    }

    fun deleteMyFriend(friendId: String, myUid: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.deleteMyFriend(friendId, myUid)
            }
            result.await()
            withContext(MainScope().coroutineContext) {
                deleteMyFriendResult.value = result.await()
            }
        }
    }

    fun setAliasToMyFriend(alias: String, friendId: String, myUid: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.setAliasToMyFriend(alias, friendId, myUid)
            }
            result.await()
            withContext(MainScope().coroutineContext) {
                aliasMyFriendResult.value = result.await()
            }
        }
    }

    data class FriendCheckDTO(val isChecked: Boolean, val friendDTO: FriendDTO)
}