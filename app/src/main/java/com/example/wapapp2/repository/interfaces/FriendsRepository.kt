package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.commons.interfaces.NewSnapshotListener
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.UserDTO
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

interface FriendsRepository {
    suspend fun addToMyFriend(friendDTO: FriendDTO): Boolean
    suspend fun loadMyFriends(): MutableList<FriendDTO>
    suspend fun removeMyFriend(friendId: String): Boolean
    suspend fun setAliasToMyFriend(alias: String, friendId: String): Boolean
}