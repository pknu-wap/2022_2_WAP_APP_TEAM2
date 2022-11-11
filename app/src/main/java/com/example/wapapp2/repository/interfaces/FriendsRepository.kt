package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.UserDTO

interface FriendsRepository {
    suspend fun getMyFriends(): MutableList<FriendDTO>
    suspend fun findUsers(email: String): MutableSet<UserDTO>
    suspend fun addToMyFriend(userDTO: UserDTO, myUid: String): Boolean
    suspend fun deleteMyFriend(friendId: String, myUid: String): Boolean
    suspend fun setAliasToMyFriend(alias: String, friendId: String, myUid: String): Boolean
}