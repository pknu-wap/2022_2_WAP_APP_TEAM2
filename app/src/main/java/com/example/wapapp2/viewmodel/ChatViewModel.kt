package com.example.wapapp2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.send.SendFcmChatDTO
import com.example.wapapp2.repository.ChatRepositorylmpl
import com.example.wapapp2.repository.FcmRepositoryImpl
import com.example.wapapp2.repository.interfaces.ChatRepository
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class ChatViewModel : ViewModel() {
    lateinit var roomId: String
    private val chatRepository: ChatRepository = ChatRepositorylmpl.getINSTANCE()
    var listenerRegistration: ListenerRegistration? = null

    val isEmptyChats = MutableLiveData<Boolean>()

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }


    fun sendMsg(chatDTO: ChatDTO, sendMsgOnFailCallback: SendMsgOnFailCallback) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async { chatRepository.sendMsg(roomId, chatDTO) }
            if (result.await().not()) {
                sendMsgOnFailCallback.OnFail()
            }
        }
    }


    fun getQueryForOption(roomId: String): Query {
        val query = Firebase.firestore
                .collection(FireStoreNames.calc_rooms.name)
                .document(roomId)
                .collection(FireStoreNames.chats.name)
                .orderBy("sendedTime", Query.Direction.DESCENDING)
        return query
    }

    fun addSnapshot(roomId: String, listener: EventListener<QuerySnapshot>) {
        listenerRegistration?.remove()
        listenerRegistration = Firebase.firestore
                .collection(FireStoreNames.calc_rooms.name)
                .document(roomId)
                .collection(FireStoreNames.chats.name)
                .orderBy("sendedTime", Query.Direction.DESCENDING)
                .limit(20)
                .addSnapshotListener(listener)
    }


    /**
     * 새 채팅 내역 알림
     */
    fun sendChat(chatDTO: ChatDTO, calcRoomDTO: CalcRoomDTO) {
        CoroutineScope(Dispatchers.IO).launch {
            val calcRoomId = calcRoomDTO.id!!
            val sendFcmChatDTO = SendFcmChatDTO(chatDTO, calcRoomDTO.name, calcRoomId)
            FcmRepositoryImpl.sendFcmToTopic(NotificationType.Chat, calcRoomId, sendFcmChatDTO)
        }
    }

    fun isEmptyChatList() {
        CoroutineScope(Dispatchers.IO).launch {
            val isEmpty = async {
                chatRepository.isEmptyChatList(roomId)
            }
            isEmpty.await()

            withContext(Main) {
                isEmptyChats.value = isEmpty.await()
            }
        }
    }

    fun interface SendMsgOnFailCallback {
        fun OnFail()
    }
}