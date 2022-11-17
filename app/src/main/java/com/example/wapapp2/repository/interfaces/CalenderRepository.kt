package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ReceiptDTO

interface CalenderRepository {
    suspend fun loadMyReceipts(myCalcRoom : CalcRoomDTO) : MutableSet<ReceiptDTO>
}