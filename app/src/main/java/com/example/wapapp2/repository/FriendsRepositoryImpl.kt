package com.example.wapapp2.repository

import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.commons.interfaces.NewSnapshotListener
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.interfaces.FriendsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FriendsRepositoryImpl private constructor() : FriendsRepository {
    val searchResultFriendsLiveData: MutableLiveData<ArrayList<FriendDTO>> = MutableLiveData<ArrayList<FriendDTO>>()
    val dummyFriendsList = DummyData.getMyFriendsList()
    private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private var INSTANCE: FriendsRepositoryImpl? = null

        fun initialize() {
            INSTANCE = FriendsRepositoryImpl()
        }

        fun getINSTANCE() = INSTANCE
    }

    fun getFriendsList(word: String) {
        if (word.isEmpty()) {
            searchResultFriendsLiveData.value = dummyFriendsList
        } else {
            val list = ArrayList<FriendDTO>()
            for (v in dummyFriendsList) {
                if (v.friendName == word) {
                    list.add(v)
                }
            }

            searchResultFriendsLiveData.value = list
        }
    }

    override suspend fun getMyFriends() = suspendCoroutine<MutableList<FriendDTO>> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name).document(auth.currentUser?.uid!!)
                .collection(FireStoreNames.myFriends.name)
        collection.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = it.result.documents.toMutableList()
                val dtoList = mutableListOf<FriendDTO>()

                for (v in list) {
                    dtoList.add(v.toObject<FriendDTO>()!!)
                }

                continuation.resume(dtoList)
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

    override fun addMyFriendsSnapshotListener(
            snapShotListener: NewSnapshotListener<List<FriendDTO>>,
    ) = fireStore.collection(FireStoreNames.users
            .name).document(auth.currentUser?.uid!!)
            .collection(FireStoreNames.myFriends.name).addSnapshotListener { snapShot, error ->
                //snapShotListener.onEvent(snapShot, error)
            }


}