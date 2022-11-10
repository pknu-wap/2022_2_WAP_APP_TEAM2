package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.repository.ChatRepository
import com.example.wapapp2.view.chat.`interface`.OnChatRecievedCallback
import com.google.firebase.firestore.FirebaseFirestore

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val observer : = LiveData<OnChatRecievedCallback>()
    private var chatList = MutableLiveData<ArrayList<ChatDTO>>()
    private val chatRepository = ChatRepository.getINSTANCE()



}