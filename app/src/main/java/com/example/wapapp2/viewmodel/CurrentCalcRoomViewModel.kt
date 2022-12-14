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
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.model.notifications.send.SendFcmCalcRoomDTO
import com.example.wapapp2.repository.CalcRoomRepositorylmpl
import com.example.wapapp2.repository.FcmRepositoryImpl
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
    val participantMap = MutableLiveData(mutableMapOf<String, CalcRoomParticipantDTO>())
    val receipts = MutableLiveData<ArrayMap<String, ReceiptDTO>>(arrayMapOf())
    val calcRoom = MutableLiveData<CalcRoomDTO>()

    private var calcRoomListenerRegistration: ListenerRegistration? = null
    private var receiptsListenerRegistration: ListenerRegistration? = null

    var exitFromRoom = false

    override fun onCleared() {
        super.onCleared()
        calcRoomListenerRegistration?.remove()
        receiptsListenerRegistration?.remove()
    }

    /**
     * ????????? ??????, ????????? ??????, ????????? ?????? ????????????
     */
    fun loadCalcRoomData(roomId: String) {
        this.roomId = roomId
        //????????? ??????, ????????? ?????? ??????
        addSnapshotCalcRoom()
        addSnapshotReceipts()
    }

    private fun onChangedParticipants(participantIds: MutableList<String>) {
        // ????????? ????????? ?????? ????????????
        viewModelScope.launch {
            val downloadedParticipantList = async {
                userRepository.getUsers(participantIds)
            }
            // ????????? ?????? ???????????? ?????? ?????? ?????? ??????
            val participantList = mutableListOf<CalcRoomParticipantDTO>()
            var name = ""
            var isMyFriend = false
            var dto: CalcRoomParticipantDTO? = null

            val _participantMap = participantMap.value!!
            _participantMap.clear()

            for (v in downloadedParticipantList.await()) {
                //??? ???????????? ??????
                isMyFriend = myFriendMap.containsKey(v.id)
                name = if (isMyFriend) myFriendMap[v.id]!!.alias else v.name

                dto = CalcRoomParticipantDTO(v.id, name, isMyFriend, v.email, v.fcmToken)
                _participantMap[v.id] = dto
            }

            withContext(Main) {
                participantMap.value = _participantMap
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

            //??????, ????????? ?????? ?????????
            val calcRoomDTO = value.toObject<CalcRoomDTO>()!!
            calcRoomDTO.id = value.id

            //????????? ?????? ??????
            if (calcRoomDTO.participantIds.toMutableSet() != participantMap.value!!.keys) {
                //????????? ?????? ??????
                val participantIds = calcRoomDTO.participantIds.toMutableList()
                onChangedParticipants(participantIds)
            }

            calcRoom.value = calcRoomDTO
        })
    }

    fun exitFromRoom(roomId: String) {
        //??? ?????????
        //calcRoom?????? ??? participantIds?????? ??? id??????
        exitFromRoom = true
        FcmRepositoryImpl.unSubscribeToTopic(roomId)

        CoroutineScope(Dispatchers.IO).launch {
            //users?????? ??? myCalcRoomIds?????? ???????????? ????????? id ??????
            userRepository.removeCalcRoomId(roomId)
            calcRoomRepository.exitFromCalcRoom(roomId)
        }
    }

    fun inviteFriends(list: MutableList<FriendDTO>, roomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            //????????? ???????????? fcm?????? ????????????
            val ids = mutableListOf<String>()
            for (f in list) {
                ids.add(f.friendUserId)
            }

            val usersResult = async {
                userRepository.getUsers(ids)
            }
            usersResult.await()

            val tokens = mutableListOf<String>()
            val users = usersResult.await()

            for (user in users) {
                tokens.add(user.fcmToken)
            }

            calcRoomRepository.inviteFriends(list, roomId)
            //???????????? ??????????????? ?????? ?????? ?????????
            sendNewCalcRoomFcm(roomId, tokens)
        }
    }

    /**
     * ????????? ?????? ??????
     */
    private suspend fun sendNewCalcRoomFcm(calcRoomId: String, recipientTokens: MutableList<String>) {
        FcmRepositoryImpl.sendFcmToRecipients(NotificationType.NewCalcRoom, recipientTokens, SendFcmCalcRoomDTO(calcRoomId))
    }


}