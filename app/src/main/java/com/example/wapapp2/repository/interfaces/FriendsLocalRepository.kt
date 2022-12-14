package com.example.wapapp2.repository.interfaces

import androidx.room.*
import com.example.wapapp2.model.FriendDTO
import kotlinx.coroutines.flow.Flow

interface FriendsLocalRepository {
    suspend fun insert(friendDTO: FriendDTO): Long

    suspend fun update(friendDTO: FriendDTO): Int

    suspend fun delete(friendDTO: FriendDTO)

    fun get(friendId: String): Flow<FriendDTO?>

    fun count(): Flow<Int>

    fun get(friendIds: List<String>): Flow<List<FriendDTO>>
    fun getAll(): Flow<List<FriendDTO>>

}