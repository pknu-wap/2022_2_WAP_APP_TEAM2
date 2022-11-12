package com.example.wapapp2.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.FriendsRepositoryImpl
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class FriendsViewModel : ViewModel() {
    private val friendsRepositoryImpl: FriendsRepositoryImpl = FriendsRepositoryImpl.getINSTANCE()!!
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    val friendsListLiveData: MutableLiveData<ArrayList<FriendDTO>> = MutableLiveData<ArrayList<FriendDTO>>(ArrayList<FriendDTO>())
    val friendCheckedLiveData: MutableLiveData<FriendCheckDTO> = MutableLiveData<FriendCheckDTO>()
    val searchResultFriendsLiveData: LiveData<ArrayList<FriendDTO>> = friendsRepositoryImpl.searchResultFriendsLiveData
    val currentRoomFriendsSet = HashSet<String>()

    val searchUsersResult = MutableLiveData<MutableList<UserDTO>>()

    val myFriendsIdSet = hashSetOf<String>()

    val addMyFriendResult = MutableLiveData<Boolean>()

    private val _deleteMyFriendResult = MutableLiveData<Boolean>()
    val deleteMyFriendResult get() = _deleteMyFriendResult

    private val _aliasMyFriendResult = MutableLiveData<Boolean>()
    val aliasMyFriendResult get() = _aliasMyFriendResult

    fun checkedFriend(friendDTO: FriendDTO, isChecked: Boolean) {
        val list = friendsListLiveData.value!!
        var contains = false
        var idx = 0
        for ((index, value) in list.withIndex()) {
            if (value.friendUserId == friendDTO.friendUserId) {
                idx = index
                contains = true
                break
            }
        }

        if (isChecked) {
            if (!contains) {
                list.add(friendDTO)
                friendsListLiveData.value = list
                friendCheckedLiveData.value = FriendCheckDTO(isChecked, friendDTO)
            }
        } else
            if (contains) {
                list.removeAt(idx)
                friendsListLiveData.value = list
                friendCheckedLiveData.value = FriendCheckDTO(isChecked, friendDTO)
            }
    }

    fun getFriends(word: String) {
        friendsRepositoryImpl.getFriendsList(word)
    }


    fun findUsers(email: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.findUsers(email)
            }
            val dtoSet = result.await()
            for (v in dtoSet) {
                if (myFriendsIdSet.contains(v.id)) {
                    dtoSet.remove(v)
                }
            }
            withContext(Main) {
                searchUsersResult.value = dtoSet.toMutableList()
            }
        }
    }

    fun addToMyFriend(userDTO: UserDTO) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.addToMyFriend(
                        FriendDTO(userDTO.id, userDTO.name, "", userDTO.email)
                )
            }
            result.await()
            withContext(Main) {
                addMyFriendResult.value = result.await()
            }
        }
    }

    fun deleteMyFriend(friendId: String, myUid: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.deleteMyFriend(friendId, myUid)
            }
            result.await()
            withContext(MainScope().coroutineContext) {
                deleteMyFriendResult.value = result.await()
            }
        }
    }

    fun setAliasToMyFriend(alias: String, friendId: String, myUid: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.setAliasToMyFriend(alias, friendId, myUid)
            }
            result.await()
            withContext(MainScope().coroutineContext) {
                aliasMyFriendResult.value = result.await()
            }
        }
    }

    fun getMyFriendsOptions(): FirestoreRecyclerOptions<FriendDTO> {
        val query =
                fireStore.collection(FireStoreNames.users.name)
                        .document(firebaseAuth.currentUser?.uid!!)
                        .collection(FireStoreNames.myFriends.name).orderBy("alias", Query.Direction.ASCENDING)

        val option = FirestoreRecyclerOptions.Builder<FriendDTO>()
                .setQuery(query) {
                    val dto = it.toObject<FriendDTO>()!!
                    myFriendsIdSet.add(dto.friendUserId)
                    dto
                }
                .build()
        return option
    }

    data class FriendCheckDTO(val isChecked: Boolean, val friendDTO: FriendDTO)
}