package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.CalcRoomDTO

interface CalcRoomRepository {
    suspend fun addNewCalcRoom(calcRoomDTO: CalcRoomDTO)
    suspend fun deleteCalcRoom(calcRoomDTO: CalcRoomDTO)
}