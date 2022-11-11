package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.repository.ChatRepositorylmpl
import com.example.wapapp2.repository.interfaces.ChatRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    //private val observer : = LiveData<OnChatRecievedCallback>()
    private var EnableChatRoom : CalcRoomDTO? = null
    private val chatRepository : ChatRepository = ChatRepositorylmpl.getINSTANCE()


    fun attach(calcRoomDTO: CalcRoomDTO){
        EnableChatRoom = calcRoomDTO
    }

    fun detach(calcRoomDTO: CalcRoomDTO){
        EnableChatRoom = null
    }

    fun sendMsg(chatDTO: ChatDTO){
        CoroutineScope(Dispatchers.Default).launch{
            chatRepository.sendMsg(EnableChatRoom!!, chatDTO)
        }
    }

    suspend fun getOptions() : FirestoreRecyclerOptions<ChatDTO> {
        return chatRepository.getRecyclerviewOptions(EnableChatRoom!!)
    }



    inner class ChatLiveData(val documentReference: DocumentReference) : LiveData<ChatDTO>(), EventListener<DocumentSnapshot>{
        private var snapshotListener: ListenerRegistration? = null

        override fun onActive() {
            super.onActive()
            snapshotListener = documentReference.addSnapshotListener(this)
        }

        override fun onInactive() {
            super.onInactive()
            snapshotListener?.remove()
        }


        override fun onEvent(result: DocumentSnapshot?, error: FirebaseFirestoreException?) {
            val item = result?.let { document ->
                document.toObject(ChatDTO::class.java)
            }
            value = item!!
        }
    }


}