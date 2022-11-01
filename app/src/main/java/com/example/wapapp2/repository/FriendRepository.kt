package com.example.wapapp2.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.FriendDTO

class FriendRepository private constructor() {
    val searchResultFriendsLiveData: MutableLiveData<ArrayList<FriendDTO>> = MutableLiveData<ArrayList<FriendDTO>>()
    val dummyFriendsList = DummyData.getFriendsList()

    companion object {
        private lateinit var INSTANCE: FriendRepository

        fun initialize() {
            INSTANCE = FriendRepository()
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