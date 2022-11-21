package com.example.wapapp2.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.dao.FriendDao
import com.example.wapapp2.repository.interfaces.FriendsLocalRepository
import com.example.wapapp2.room.AppRoom
import kotlinx.coroutines.flow.Flow

class FriendsLocalRepositoryImpl private constructor(context: Context) : FriendsLocalRepository {
    private val dao: FriendDao

    init {
        dao = AppRoom.getINSTANCE(context).friendDAO()
    }

    companion object {
        private var INSTANCE: FriendsLocalRepositoryImpl? = null

        fun initialize(context: Context) {
            if (INSTANCE == null)
                INSTANCE = FriendsLocalRepositoryImpl(context)
        }

        fun getINSTANCE() = INSTANCE!!
    }

    @WorkerThread
    override suspend fun insert(friendDTO: FriendDTO): Long {
        return dao.insert(friendDTO)
    }

    @WorkerThread
    override suspend fun update(friendDTO: FriendDTO): Int {
        return dao.update(friendDTO)
    }

    @WorkerThread
    override suspend fun delete(friendDTO: FriendDTO) {
        dao.delete(friendDTO)
    }

    override fun get(friendId: Int): Flow<FriendDTO> = dao.get(friendId)

    override fun get(friendIds: List<String>): Flow<List<FriendDTO>> = dao.get(friendIds)

}
