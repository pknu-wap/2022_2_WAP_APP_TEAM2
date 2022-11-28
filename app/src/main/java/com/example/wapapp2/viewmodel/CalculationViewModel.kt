package com.example.wapapp2.viewmodel

import android.util.Log
import androidx.collection.arrayMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.model.ReceiptProductParticipantDTO
import com.example.wapapp2.repository.CalcRoomRepositorylmpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.example.wapapp2.view.calculation.receipt.interfaces.IProductCheckBox
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class CalculationViewModel : ViewModel(), IProductCheckBox {
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE
    private val calcRoomRepository = CalcRoomRepositorylmpl.getINSTANCE()

    private var onGoingReceiptIdsListener: ListenerRegistration? = null
    private val productsListenerMap = mutableMapOf<String, ListenerRegistration>()

    //내 정산 금액
    val mySettlementAmount = MutableLiveData(0)
    val receiptMap = MutableLiveData(arrayMapOf<String, ReceiptDTO>())

    lateinit var myUid: String
    lateinit var myUserName: String

    lateinit var calcRoomId: String

    override fun onCleared() {
        super.onCleared()
        onGoingReceiptIdsListener?.remove()
        for (listener in productsListenerMap.values) {
            listener.remove()
        }
        productsListenerMap.clear()
    }

    override fun updateMyTransferMoney(): Int {
        var myMoney = 0
        for (receipt in receiptMap.value!!.values) {
            val payersId = receipt.payersId

            for (product in receipt.productMap.values) {
                for (participant in product.participants) {

                    if (participant.key == myUid) {
                        if (payersId == myUid) {
                            myMoney -= (product.price / product.participants.size)
                        } else {
                            myMoney += (product.price / product.participants.size)
                        }
                    }

                }

            }
        }
        return myMoney
    }

    private fun updateSettlementAmount(isChecked: Boolean, productDTO: ReceiptProductDTO) {
        // 로그인한 사람이 결제자 -> 체크박스 체크 : - , unchecked : +
        val payersIsMe = (productDTO.payersId == myUid)
        val increase = if (isChecked) !payersIsMe else payersIsMe

        val numOfPeopleSelected = if (isChecked) productDTO.numOfPeopleSelected
        else productDTO.numOfPeopleSelected + 1

        val newAmount = if (increase) {
            mySettlementAmount.value!! + (productDTO.price / numOfPeopleSelected)
        } else {
            mySettlementAmount.value!! - (productDTO.price / numOfPeopleSelected)
        }
        mySettlementAmount.value = newAmount
        Log.e("금액 변경됨", "체크여부 : $isChecked , 항목명 : ${productDTO.name} , 체크 인원 수 : ${productDTO.numOfPeopleSelected} " +
                ", 금액 : ${productDTO.price} , 계산 금액 : $newAmount")
    }

    override fun onProductChecked(productDTO: ReceiptProductDTO) {
        updateSettlementAmount(isChecked = true, productDTO)

        CoroutineScope(Dispatchers.IO).launch {
            receiptRepository.updateMyIdFromProductParticipantIds(true,
                    calcRoomId, productDTO.receiptId!!, productDTO.id, ReceiptProductParticipantDTO(myUid, myUserName, false, ""))
        }

    }

    override fun onProductUnchecked(productDTO: ReceiptProductDTO) {
        updateSettlementAmount(isChecked = false, productDTO)

        CoroutineScope(Dispatchers.IO).launch {
            receiptRepository.updateMyIdFromProductParticipantIds(false,
                    calcRoomId, productDTO.receiptId!!, productDTO.id,
                    ReceiptProductParticipantDTO(myUid, myUserName, false, ""))
        }

    }

    /**
     * 내 최종 정산 금액 계산
     **/
    private fun updateMyFinalSettlementAmount() {

    }

    fun loadOngoingReceiptIds() {
        onGoingReceiptIdsListener?.remove()
        onGoingReceiptIdsListener = calcRoomRepository.snapshotCalcRoom(calcRoomId) { value, error ->
            if (value == null) {
                return@snapshotCalcRoom
            }

            CoroutineScope(Dispatchers.IO).launch {
                //성공, 정산방 정보 가져옴
                val calcRoomDTO = value.toObject<CalcRoomDTO>()!!

                val lastOngoingReceiptIds = receiptMap.value!!.keys.toMutableSet()
                val loadedOngoingReceiptIds = calcRoomDTO.ongoingReceiptIds.toMutableSet()

                //변화 확인
                //추가된 영수증
                val addedReceiptIds = loadedOngoingReceiptIds.subtract(lastOngoingReceiptIds)
                //삭제된 영수증
                val removedReceiptIds = lastOngoingReceiptIds.subtract(loadedOngoingReceiptIds)

                if (removedReceiptIds.isNotEmpty()) {
                    // 삭제된 영수증들을 영수증map에서 삭제
                    val modifiedMap = receiptMap.value!!
                    modifiedMap.removeAll(removedReceiptIds)

                    withContext(Main) {
                        receiptMap.value = modifiedMap
                    }
                }
                if (addedReceiptIds.isNotEmpty()) {
                    //영수증 정보 로드
                    loadReceipts(addedReceiptIds)
                }
            }

        }
    }

    private suspend fun loadReceipts(receiptIds: Set<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val receiptListResult = async { receiptRepository.getReceipts(calcRoomId, receiptIds.toList()) }
            val receiptList = receiptListResult.await()

            for (receipt in receiptList) {
                val receiptId = receipt.id
                if (productsListenerMap.containsKey(receiptId))
                    continue

                val productListener = receiptRepository.snapshotProducts(calcRoomId, receiptId) { value, error ->
                    if (value == null)
                        return@snapshotProducts

                    //영수증 항목 로드, key = receiptId, value : map   key = productId
                    val productMap = mutableMapOf<String, ReceiptProductDTO>()
                    val modifiedReceiptMap = receiptMap.value!!
                    val removedProductIds = mutableSetOf<String>()
                    var firstLoadedData = true

                    for (dc in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED || dc.type == DocumentChange.Type.MODIFIED) {
                            val productDto = dc.document.toObject<ReceiptProductDTO>()
                            productDto.numOfPeopleSelected = productDto.participants.size
                            productDto.id = dc.document.id
                            productDto.payersId = receipt.payersId
                            productDto.receiptId = receipt.id

                            productMap[productDto.id] = productDto

                            if (dc.type == DocumentChange.Type.MODIFIED)
                                firstLoadedData = false
                        } else {
                            val removedProductId = dc.document.id
                            removedProductIds.add(removedProductId)
                        }

                    }

                    if (!modifiedReceiptMap.containsKey(receiptId))
                        modifiedReceiptMap[receipt.id] = receipt

                    modifiedReceiptMap[receiptId]!!.apply {
                        if (removedProductIds.isNotEmpty())
                            this.productMap.removeAll(removedProductIds)

                        this.productMap.putAll(productMap.toMutableMap())
                    }


                    receiptMap.value = modifiedReceiptMap
                    if (firstLoadedData) {
                        //모든 영수증이 로드된 직후
                        val myMoney = updateMyTransferMoney()
                        mySettlementAmount.value = myMoney
                    }
                }

                productsListenerMap[receiptId]?.apply {
                    remove()
                }
                productsListenerMap[receiptId] = productListener
            }

        }
    }


}