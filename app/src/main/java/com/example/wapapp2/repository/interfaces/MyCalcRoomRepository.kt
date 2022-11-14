package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.CalcRoomDTO

interface MyCalcRoomRepository {
    suspend fun getMyCalcRooms(): MutableMap<String, CalcRoomDTO>
    suspend fun exitFromCalcRoom(roomId: String): Boolean
}