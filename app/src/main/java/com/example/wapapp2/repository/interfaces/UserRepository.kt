package com.example.wapapp2.repository.interfaces

import android.net.Uri
import com.example.wapapp2.model.UserDTO
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun findUsers(email: String): MutableSet<UserDTO>
    suspend fun getUsers(ids: MutableList<String>): MutableList<UserDTO>
    suspend fun getUser(userId: String): UserDTO?
    suspend fun removeCalcRoomId(roomId: String)
    suspend fun setMyProfileUrl(url: String): Boolean
}