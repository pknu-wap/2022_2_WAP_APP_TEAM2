package com.example.wapapp2.viewmodel

import androidx.collection.arrayMapOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.CalcRoomParticipantDTO
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.FriendsRepositoryImpl
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*

class FriendsViewModel : ViewModel() {
    private val friendsRepositoryImpl: FriendsRepositoryImpl = FriendsRepositoryImpl.getINSTANCE()!!
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    val friendsListLiveData = MutableLiveData<ArrayList<FriendDTO>>(ArrayList<FriendDTO>())
    val friendCheckedLiveData = MutableLiveData<FriendCheckDTO>()
    val searchResultFriendsLiveData = friendsRepositoryImpl.searchResultFriendsLiveData
    val participantsInCalcRoom = mutableListOf<CalcRoomParticipantDTO>()

    val myFriendMap = mutableMapOf<String, FriendDTO>()

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


    fun addToMyFriend(userDTO: UserDTO) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                friendsRepositoryImpl.addToMyFriend(
                        FriendDTO(userDTO.id, userDTO.name, "", userDTO.email)
                )
            }
            result.await()
        }
    }

    fun removeMyFriend(friendId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            //myFriendsIdSet에서 삭제
            myFriendMap.remove(friendId)

            val result = async {
                friendsRepositoryImpl.removeMyFriend(friendId)
            }
            result.await()
        }
    }


    fun getMyFriendsOptions(): FirestoreRecyclerOptions<FriendDTO> {
        val query =
                fireStore.collection(FireStoreNames.users.name)
                        .document(firebaseAuth.currentUser?.uid!!)
                        .collection(FireStoreNames.myFriends.name).orderBy("alias", Query.Direction.ASCENDING)

        val option = FirestoreRecyclerOptions.Builder<FriendDTO>()
                .setQuery(query, MetadataChanges.INCLUDE) {
                    val dto = it.toObject<FriendDTO>()!!
                    myFriendMap[dto.friendUserId] = dto
                    dto
                }
                .build()
        return option
    }

    data class FriendCheckDTO(val isChecked: Boolean, val friendDTO: FriendDTO)
}