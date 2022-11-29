package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.repository.interfaces.FriendsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FriendsRepositoryImpl private constructor() : FriendsRepository {
    private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private var INSTANCE: FriendsRepositoryImpl? = null

        fun initialize() {
            INSTANCE = FriendsRepositoryImpl()
        }

        fun getINSTANCE() = INSTANCE!!
    }


    override suspend fun loadMyFriends() = suspendCoroutine<MutableList<FriendDTO>> { continuation ->
        fireStore.collection(FireStoreNames.users.name)
                .document(auth.currentUser?.uid!!)
                .collection(FireStoreNames.myFriends.name).orderBy("alias", Query.Direction.ASCENDING).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val list = mutableListOf<FriendDTO>()
                        for (d in it.result.documents) {
                            list.add(d.toObject<FriendDTO>()!!)
                        }
                        continuation.resume(list)
                    } else {
                        continuation.resume(mutableListOf())
                    }
                }
    }


    override suspend fun addToMyFriend(friendDTO: FriendDTO) = suspendCoroutine<Boolean> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name).document(auth.currentUser?.uid!!).collection("myFriends")
        collection.document(friendDTO.friendUserId).set(friendDTO).addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
    }

    override suspend fun removeMyFriend(friendId: String) = suspendCoroutine<Boolean> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name).document(auth.currentUser?.uid!!).collection("myFriends")
        collection.document(friendId).delete().addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
    }

    override suspend fun setAliasToMyFriend(alias: String, friendId: String) = suspendCoroutine<Boolean> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name).document(auth.currentUser?.uid!!).collection("myFriends")
        collection.document(friendId).update("alias", alias).addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
    }


    override fun addSnapShotListenerToFriend(id : String, eventListener: EventListener<DocumentSnapshot>) : ListenerRegistration =
        fireStore.collection(FireStoreNames.users.name).document(id).addSnapshotListener(eventListener)

}