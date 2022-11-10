package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.repository.FriendsRepository

class FriendsViewModel(application: Application) : AndroidViewModel(application) {
    private val friendsRepository: FriendsRepository = FriendsRepository.getINSTANCE()

    val friendsListLiveData: MutableLiveData<ArrayList<FriendDTO>> = MutableLiveData<ArrayList<FriendDTO>>(ArrayList<FriendDTO>())
    val friendCheckedLiveData: MutableLiveData<FriendCheckDTO> = MutableLiveData<FriendCheckDTO>()
    val searchResultFriendsLiveData: LiveData<ArrayList<FriendDTO>> = friendsRepository.searchResultFriendsLiveData
    val currentRoomFriendsSet = HashSet<String>()

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
        friendsRepository.getFriendsList(word)
    }

    data class FriendCheckDTO(val isChecked: Boolean, val friendDTO: FriendDTO)
}