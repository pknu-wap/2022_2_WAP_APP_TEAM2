package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.repository.FriendRepository

class NewCalcRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val friendRepository: FriendRepository = FriendRepository.getINSTANCE()

    val friendsListLiveData: MutableLiveData<ArrayList<FriendDTO>> = MutableLiveData<ArrayList<FriendDTO>>(ArrayList<FriendDTO>())
    val friendCheckedLiveData: MutableLiveData<FriendCheckDTO> = MutableLiveData<FriendCheckDTO>()
    val searchResultFriendsLiveData: LiveData<ArrayList<FriendDTO>> = friendRepository.searchResultFriendsLiveData

    fun checkedFriend(friendDTO: FriendDTO, isChecked: Boolean) {
        val list = friendsListLiveData.value!!
        var contains = false
        var idx = 0
        for ((index, value) in list.withIndex()) {
            if (value.uid == friendDTO.uid) {
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
        friendRepository.getFriendsList(word)
    }

    data class FriendCheckDTO(val isChecked: Boolean, val friendDTO: FriendDTO)
}