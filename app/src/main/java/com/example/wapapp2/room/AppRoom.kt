package com.example.wapapp2.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.dao.FriendDao

@Database(entities = [FriendDTO::class], version = 1)
abstract class AppRoom : RoomDatabase() {
    companion object {
        var INSTANCE: AppRoom? = null

        fun getINSTANCE(context: Context): AppRoom {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context, AppRoom::class.java, "appdb")
                        .build()
            return INSTANCE!!
        }
    }

    abstract fun friendDAO(): FriendDao
}