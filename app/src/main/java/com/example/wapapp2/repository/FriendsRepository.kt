package com.example.wapapp2.repository

import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.FriendDTO

class FriendsRepository private constructor() {
    val searchResultFriendsLiveData: MutableLiveData<ArrayList<FriendDTO>> = MutableLiveData<ArrayList<FriendDTO>>()
    val dummyFriendsList = DummyData.getMyFriendsList()

    companion object {
        private lateinit var INSTANCE: FriendsRepository

        fun initialize() {
            INSTANCE = FriendsRepository()
        }

        fun getINSTANCE() = INSTANCE
    }

    fun getFriendsList(word: String) {
        if (word.isEmpty()) {
            searchResultFriendsLiveData.value = dummyFriendsList
        } else {
            val list = ArrayList<FriendDTO>()
            for (v in dummyFriendsList) {
                if (v.friendName == word) {
                    list.add(v)
                }
            }

            searchResultFriendsLiveData.value = list
        }
    }
}