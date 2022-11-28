package com.example.wapapp2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wapapp2.repository.FriendsRepositoryImpl
import com.example.wapapp2.repository.interfaces.FriendsRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

class FriendsProfileViewModel : ViewModel() {
    private val friendsRepository: FriendsRepository = FriendsRepositoryImpl.getINSTANCE()
    private val myCalcRoomReceiptListeners : ArrayList<ListenerRegistration> = arrayListOf()

    fun loadFriendProfile(id : String,eventListener: EventListener<DocumentSnapshot>){
        myCalcRoomReceiptListeners.add(friendsRepository.addSnapShotListenerToFriend(id,eventListener))
    }


    override fun onCleared() {
        super.onCleared()
        if(myCalcRoomReceiptListeners.isEmpty().not())
            for (registered in myCalcRoomReceiptListeners)
                registered.remove()
    }
}