package com.example.wapapp2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.CalcRoomParticipantDTO
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.repository.CalcRoomRepositorylmpl
import com.example.wapapp2.repository.MyCalcRoomRepositoryImpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.example.wapapp2.repository.UserRepositoryImpl
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class CurrentCalcRoomViewModel : ViewModel() {
    private val myCalcRoomRepository = MyCalcRoomRepositoryImpl.getINSTANCE()
    private val calcRoomRepositorylmpl = CalcRoomRepositorylmpl.getINSTANCE()
    private val userRepository = UserRepositoryImpl.getINSTANCE()
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE

    var roomId: String? = null
    var myFriendsMap = mutableMapOf<String, FriendDTO>()
    private val participantIds = mutableSetOf<String>()

    val participants = MutableLiveData<MutableList<CalcRoomParticipantDTO>>()

    private var calcRoomListenerRegistration: ListenerRegistration? = null

    override fun onCleared() {
        super.onCleared()
    }

    /**
     * 정산방 정보, 참여자 목록, 영수증 목록 불러오기
     */
    fun loadCalcRoomData(roomId: String) {
        this.roomId = roomId
        addSnapshotToCalcRoom()

        viewModelScope.launch {
            // 정산방 정보 가져오기
            val calcRoomDTO = async {
                myCalcRoomRepository.getCalcRoom(roomId)
            }
            calcRoomDTO.await()


        }
    }

    private fun onChangedParticipants(calcRoomDTO: CalcRoomDTO) {
        // 정산방 참여자 목록 가져오기
        viewModelScope.launch {
            val downloadedParticipantList = async {
                userRepository.getUsers(participantIds.toMutableList())
            }
            // 받아온 유저 목록에서 친구 추가 여부 확인
            val participantList = mutableListOf<CalcRoomParticipantDTO>()
            var name = ""
            var isMyFriend = false

            for (v in downloadedParticipantList.await()) {
                isMyFriend = myFriendsMap.containsKey(v.id)
                name = if (isMyFriend) myFriendsMap[v.id]!!.alias else v.name
                participantList.add(CalcRoomParticipantDTO(v.id, name, isMyFriend, v.email))
            }

            withContext(Main) {
                participants.value = participantList
            }
        }
    }

    private fun onChangedReceipts() {
        
    }

    private fun addSnapshotToCalcRoom() {
        calcRoomListenerRegistration?.remove()
        calcRoomListenerRegistration = calcRoomRepositorylmpl.snapshotCalcRoom(roomId!!, EventListener<DocumentSnapshot> { value, error ->
            if (error == null) {
                //성공
                val calcRoomDTO = value!!.toObject<CalcRoomDTO>()!!
                calcRoomDTO.id = value.id

                //참여자 변화 확인
                if (calcRoomDTO.participantIds.toMutableSet() != participantIds) {
                    //참여자 변화 생김
                    participantIds.clear()
                    participantIds.addAll(calcRoomDTO.participantIds.toMutableSet())
                    onChangedParticipants(calcRoomDTO)
                }

                // 영수증 목록 변화 확인
                val receipts = async {
                    receiptRepository.getReceipts(roomId)
                }
                receipts.await()
            } else {

            }
        })
    }
}