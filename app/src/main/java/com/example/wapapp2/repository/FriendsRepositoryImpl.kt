package com.example.wapapp2.repository

import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.interfaces.FriendsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FriendsRepositoryImpl private constructor() : FriendsRepository {
    val searchResultFriendsLiveData: MutableLiveData<ArrayList<FriendDTO>> = MutableLiveData<ArrayList<FriendDTO>>()
    val dummyFriendsList = DummyData.getMyFriendsList()
    private val fireStore = FirebaseFirestore.getInstance()

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

    override suspend fun findUsers(userName: String) = suspendCoroutine<MutableList<UserDTO>> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
        collection.whereEqualTo("name", userName).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = it.result.documents.toMutableList()
                val dtoList = mutableListOf<UserDTO>()
                for (v in list) {
                    dtoList.add(v.toObject<UserDTO>()!!)
                }

                continuation.resume(dtoList)
            } else {
                continuation.resume(mutableListOf<UserDTO>())
            }
        }
    }

    override suspend fun addToMyFriend(userDTO: UserDTO, myUid: String) = suspendCoroutine<Boolean> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name).document(myUid).collection("myFriends")
        collection.document(userDTO.id).set(userDTO).addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
    }

    override suspend fun deleteMyFriend(friendId: String, myUid: String) = suspendCoroutine<Boolean> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name).document(myUid).collection("myFriends")
        collection.document(friendId).delete().addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
    }

    override suspend fun setAliasToMyFriend(alias: String, friendId: String, myUid: String) = suspendCoroutine<Boolean> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name).document(myUid).collection("myFriends")
        collection.document(friendId).update("alias", alias).addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
    }

}