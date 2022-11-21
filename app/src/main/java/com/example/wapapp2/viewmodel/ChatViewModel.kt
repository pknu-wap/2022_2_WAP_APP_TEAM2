package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.repository.ChatRepositorylmpl
import com.example.wapapp2.repository.interfaces.ChatRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private lateinit var EnableChatRoom: CalcRoomDTO
    private val chatRepository: ChatRepository = ChatRepositorylmpl.getINSTANCE()
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun attach(calcRoomDTO: CalcRoomDTO) {
        EnableChatRoom = calcRoomDTO
    }


    fun sendMsg(chatDTO: ChatDTO) {
        CoroutineScope(Dispatchers.Default).launch {
            chatRepository.sendMsg(EnableChatRoom.id!!, chatDTO)
        }
    }


    fun getQueryForOption(roomId: String): Query {
        val query = Firebase.firestore
                .collection(FireStoreNames.calc_rooms.name)
                .document(roomId)
                .collection(FireStoreNames.chats.name)
                .orderBy("sendedTime", Query.Direction.DESCENDING)

        /*
        query.addSnapshotListener(snapshotListener)
        //scroll 관련 구현 필요 snapshotlistener ??

         */
        return query
    }

    fun addSnapshot(roomId: String, listener: EventListener<QuerySnapshot>) {
        listenerRegistration = Firebase.firestore
                .collection(FireStoreNames.calc_rooms.name)
                .document(roomId)
                .collection(FireStoreNames.chats.name)
                .orderBy("sendedTime", Query.Direction.DESCENDING)
                .addSnapshotListener(listener)
    }
}