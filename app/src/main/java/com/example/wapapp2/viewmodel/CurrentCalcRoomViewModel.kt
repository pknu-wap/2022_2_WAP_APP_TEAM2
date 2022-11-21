package com.example.wapapp2.viewmodel

import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.CalcRoomParticipantDTO
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.CalcRoomRepositorylmpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.example.wapapp2.repository.UserRepositoryImpl
import com.example.wapapp2.repository.interfaces.CalcRoomRepository
import com.example.wapapp2.repository.interfaces.ReceiptRepository
import com.example.wapapp2.repository.interfaces.UserRepository
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class CurrentCalcRoomViewModel : ViewModel() {
    private val calcRoomRepository: CalcRoomRepository = CalcRoomRepositorylmpl.getINSTANCE()
    private val userRepository: UserRepository = UserRepositoryImpl.getINSTANCE()
    private val receiptRepository: ReceiptRepository = ReceiptRepositoryImpl.INSTANCE

    var roomId: String? = null
    val myFriendMap = mutableMapOf<String, FriendDTO>()
    val participants = MutableLiveData<MutableList<CalcRoomParticipantDTO>>()
    val participantMap = mutableMapOf<String, CalcRoomParticipantDTO>()
    val participantIds = mutableSetOf<String>()
    val receipts = MutableLiveData<ArrayMap<String, ReceiptDTO>>(arrayMapOf())
    val calcRoom = MutableLiveData<CalcRoomDTO>()

    private var calcRoomListenerRegistration: ListenerRegistration? = null
    private var receiptsListenerRegistration: ListenerRegistration? = null

    override fun onCleared() {
        super.onCleared()
        calcRoomListenerRegistration?.remove()
        receiptsListenerRegistration?.remove()
    }

    /**
     * 정산방 정보, 참여자 목록, 영수증 목록 불러오기
     */
    fun loadCalcRoomData(roomId: String) {
        this.roomId = roomId
        //정산방 정보, 참여자 목록 로드
        addSnapshotCalcRoom()
        addSnapshotReceipts()
    }

    private fun onChangedParticipants() {
        // 정산방 참여자 목록 가져오기
        viewModelScope.launch {
            val downloadedParticipantList = async {
                userRepository.getUsers(participantIds.toMutableList())
            }
            // 받아온 유저 목록에서 친구 추가 여부 확인
            val participantList = mutableListOf<CalcRoomParticipantDTO>()
            var name = ""
            var isMyFriend = false
            var dto: CalcRoomParticipantDTO? = null

            for (v in downloadedParticipantList.await()) {
                //내 친구인지 확인
                isMyFriend = myFriendMap.containsKey(v.id)
                name = if (isMyFriend) myFriendMap[v.id]!!.alias else v.name

                dto = CalcRoomParticipantDTO(v.id, name, isMyFriend, v.email, v.fcmToken)
                participantList.add(dto)
                participantMap[v.id] = dto
            }

            withContext(Main) {
                participants.value = participantList
            }
        }
    }

    private fun addSnapshotReceipts() {
        receiptsListenerRegistration?.remove()
        receiptsListenerRegistration =
                receiptRepository.snapshotReceipts(roomId!!, EventListener<QuerySnapshot> { value, error ->
                    if (value == null) {
                        return@EventListener
                    }

                    val receiptMap = receipts.value!!

                    for (dc in value.documentChanges) {
                        if (dc.type != DocumentChange.Type.REMOVED) {
                            receiptMap.remove(dc.document.id)
                        } else {
                            receiptMap[dc.document.id] = dc.document.toObject<ReceiptDTO>()
                        }
                    }
                    receipts.value = receiptMap
                })
    }

    private fun addSnapshotCalcRoom() {
        calcRoomListenerRegistration?.remove()
        calcRoomListenerRegistration = calcRoomRepository.snapshotCalcRoom(roomId!!, EventListener<DocumentSnapshot> { value, error ->
            if (value == null) {
                return@EventListener
            }

            //성공, 정산방 정보 가져옴
            val calcRoomDTO = value.toObject<CalcRoomDTO>()!!
            calcRoomDTO.id = value.id

            //참여자 변화 확인
            if (calcRoomDTO.participantIds.toMutableSet() != participantIds) {
                //참여자 변화 생김
                participantIds.clear()
                participantIds.addAll(calcRoomDTO.participantIds.toMutableSet())
                onChangedParticipants()
            }

            calcRoom.value = calcRoomDTO
        })
    }

    fun exitRoom(roomId: String) {
        //방 나가기
        //calcRoom문서 내 participantIds에서 내 id삭제
        CoroutineScope(Dispatchers.Default).launch {
            //users문서 내 myCalcRoomIds에서 나가려는 정산방 id 삭제
            userRepository.removeCalcRoomId(roomId)
            calcRoomRepository.exitFromCalcRoom(roomId)
        }
    }

    fun inviteFriends(list: MutableList<FriendDTO>, roomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = async { calcRoomRepository.inviteFriends(list, roomId) }
            result.await()
        }
    }
}