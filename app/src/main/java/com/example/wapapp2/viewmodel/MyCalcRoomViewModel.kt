package com.example.wapapp2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.repository.MyCalcRoomRepositoryImpl
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MyCalcRoomViewModel : ViewModel() {
    private val myCalcRoomRepositoryImpl = MyCalcRoomRepositoryImpl.getINSTANCE()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getMyCalcRoomsOptions(): FirestoreRecyclerOptions<CalcRoomDTO> {
        val query = firestore.collection(FireStoreNames.calc_rooms.name)
                .orderBy("lastModifiedTime", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<CalcRoomDTO>()
                .setQuery(query, MetadataChanges.INCLUDE) {
                    val dto = it.toObject<CalcRoomDTO>()!!
                    dto.id = it.id
                    dto
                }.build()
        return options

    }

    fun exitFromCalcRoom(roomId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                myCalcRoomRepositoryImpl.exitFromCalcRoom(roomId)
            }
            result.await()
        }
    }
}