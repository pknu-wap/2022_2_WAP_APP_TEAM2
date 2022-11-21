package com.example.wapapp2.model.dao

import androidx.room.*
import com.example.wapapp2.model.FriendDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friendDTO: FriendDTO): Long

    @Update
    suspend fun update(friendDTO: FriendDTO): Int

    @Delete
    suspend fun delete(friendDTO: FriendDTO)

    @Query("SELECT * FROM FriendDTO WHERE friendUserId = :friendId")
    fun get(friendId: Int): Flow<FriendDTO>

    @Query("SELECT * FROM FriendDTO WHERE friendUserId IN (:friendIds)")
    fun get(friendIds: List<String>): Flow<List<FriendDTO>>
}