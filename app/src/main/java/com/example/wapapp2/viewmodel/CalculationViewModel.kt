package com.example.wapapp2.viewmodel

import android.util.Log
import androidx.collection.arrayMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.*
import com.example.wapapp2.repository.BankAccountRepositoryImpl
import com.example.wapapp2.repository.CalcRoomRepositorylmpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.example.wapapp2.view.calculation.receipt.interfaces.IProductCheckBox
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.concurrent.atomic.AtomicInteger

class CalculationViewModel : ViewModel(), IProductCheckBox {
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE
    private val calcRoomRepository = CalcRoomRepositorylmpl.getINSTANCE()
    private val bankAccountsRepository = BankAccountRepositoryImpl.getINSTANCE()

    private var onGoingReceiptIdsListener: ListenerRegistration? = null
    private val productsListenerMap = mutableMapOf<String, ListenerRegistration>()
    private val receiptsListenerMap = mutableMapOf<String, ListenerRegistration>()

    //내 정산 금액
    val mySettlementAmount = MutableLiveData(0)
    val receiptMap = MutableLiveData(arrayMapOf<String, ReceiptDTO>())
    val calcRoomParticipantIds = mutableSetOf<String>()

    val finalTransferData = MutableLiveData<MutableList<FinalTransferDTO>>()

    val onVerifiedAllParticipants = MutableLiveData<Boolean>(false)
    val verifiedParticipantIds = MutableLiveData(mutableSetOf<String>())
    val onCompletedFirstReceiptsData = MutableLiveData<Boolean>(false)

    val calculationCompletedPayerIds = MutableLiveData(mutableSetOf<String>())
    val completedAllCalc = MutableLiveData(false)

    val receiptCount = AtomicInteger(0)

    lateinit var myUid: String
    lateinit var myUserName: String

    lateinit var calcRoomId: String


    override fun onCleared() {
        super.onCleared()
        onGoingReceiptIdsListener?.remove()
        for (listener in productsListenerMap.values) {
            listener.remove()
        }
        for (listener in receiptsListenerMap.values) {
            listener.remove()
        }
        productsListenerMap.clear()
        receiptsListenerMap.clear()
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

    fun requestModifyCalculation() {
        CoroutineScope(Dispatchers.IO).launch {
            for (receipt in receiptMap.value!!.values) {
                receiptRepository.updateMyIdInCheckedParticipants(false, calcRoomId, receipt.id)
            }
        }
    }

    fun confirmMyCalculation() {
        CoroutineScope(Dispatchers.IO).launch {
            for (receipt in receiptMap.value!!.values) {
                receiptRepository.updateMyIdInCheckedParticipants(true, calcRoomId, receipt.id)
            }
        }
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
                val modifiedMap = receiptMap.value!!

                val lastOngoingReceiptIds = modifiedMap.keys.toMutableSet()
                val loadedOngoingReceiptIds = calcRoomDTO.ongoingReceiptIds.toMutableSet()
                receiptCount.set(loadedOngoingReceiptIds.size)

                //변화 확인
                //추가된 영수증
                val addedReceiptIds = loadedOngoingReceiptIds.subtract(lastOngoingReceiptIds)
                //삭제된 영수증
                val removedReceiptIds = lastOngoingReceiptIds.subtract(loadedOngoingReceiptIds)

                // 삭제된 영수증들을 영수증map에서 삭제
                modifiedMap.removeAll(removedReceiptIds)

                if (removedReceiptIds.isNotEmpty()) {
                    withContext(Main) {
                        receiptMap.value = modifiedMap
                    }
                } else if (loadedOngoingReceiptIds.isEmpty()) {
                    withContext(Main) {
                        receiptMap.value = modifiedMap
                        onCompletedFirstReceiptsData.value = true
                    }
                }

                //영수증 정보 로드
                loadReceipts(addedReceiptIds)

                if (loadedOngoingReceiptIds.isEmpty()) {
                    withContext(Main) {
                        completedAllCalc.value = true
                    }
                }
            }

        }
    }

    private fun loadReceipts(receiptIds: Set<String>) {
        for (receiptId in receiptIds) {
            val listenerRegistration = receiptRepository.snapshotReceipt(calcRoomId, receiptId) { value, error ->
                value?.also { document ->
                    val newReceiptDTO = document.toObject<ReceiptDTO>()!!
                    newReceiptDTO.id = document.id

                    if (receiptMap.value!!.containsKey(newReceiptDTO.id)) {
                        val lastReceiptMap = receiptMap.value!!
                        val lastReceiptDTO = lastReceiptMap[newReceiptDTO.id]!!

                        lastReceiptDTO.name = newReceiptDTO.name
                        lastReceiptDTO.checkedParticipantIds.clear()
                        lastReceiptDTO.checkedParticipantIds.addAll(newReceiptDTO.checkedParticipantIds)
                        lastReceiptDTO.totalMoney = newReceiptDTO.totalMoney
                        lastReceiptDTO.status = newReceiptDTO.status

                        receiptMap.value = lastReceiptMap
                    } else {
                        loadProducts(newReceiptDTO)
                    }

                    if (newReceiptDTO.status) {
                        val completedPayerIds = calculationCompletedPayerIds.value!!
                        completedPayerIds.add(newReceiptDTO.payersId)
                        calculationCompletedPayerIds.value = completedPayerIds
                    }

                    if (isCompletedLoadReceipts()) {
                        onVerifiedAllParticipants.value = checkVerifiedAllParticipants()
                    }
                }
            }

            receiptsListenerMap[receiptId] = listenerRegistration
        }
    }


    private fun loadProducts(receiptDTO: ReceiptDTO) {
        val productListener = receiptRepository.snapshotProducts(calcRoomId, receiptDTO.id) { value, error ->
            if (value == null)
                return@snapshotProducts

            //영수증 항목 로드, key = receiptId, value : map   key = productId
            val productMap = mutableMapOf<String, ReceiptProductDTO>()
            val modifiedReceiptMap = receiptMap.value!!
            val removedProductIds = mutableSetOf<String>()

            for (dc in value.documentChanges) {
                if (dc.type == DocumentChange.Type.ADDED || dc.type == DocumentChange.Type.MODIFIED) {
                    val productDto = dc.document.toObject<ReceiptProductDTO>()
                    productDto.numOfPeopleSelected = productDto.participants.size
                    productDto.id = dc.document.id
                    productDto.payersId = receiptDTO.payersId
                    productDto.receiptId = receiptDTO.id

                    productMap[productDto.id] = productDto
                } else {
                    val removedProductId = dc.document.id
                    removedProductIds.add(removedProductId)
                }
            }

            if (!modifiedReceiptMap.containsKey(receiptDTO.id))
                modifiedReceiptMap[receiptDTO.id] = receiptDTO

            modifiedReceiptMap[receiptDTO.id]!!.apply {
                this.productMap.removeAll(removedProductIds)
                this.productMap.putAll(productMap.toMutableMap())
            }

            receiptMap.value = modifiedReceiptMap
            val myMoney = updateMyTransferMoney()
            mySettlementAmount.value = myMoney

            if (isCompletedLoadProducts()) {
                onCompletedFirstReceiptsData.value = true
                onVerifiedAllParticipants.value = checkVerifiedAllParticipants()
            }
        }
        productsListenerMap[receiptDTO.id] = productListener
    }

    private fun checkVerifiedAllParticipants(): Boolean {
        val receiptMap = receiptMap.value!!
        val verifiedParticipants = mutableSetOf<String>()

        for (receipt in receiptMap.values) {
            verifiedParticipants.addAll(receipt.checkedParticipantIds.toSet())
            verifiedParticipantIds.value = verifiedParticipants

            if (receipt.checkedParticipantIds.toSet() != calcRoomParticipantIds.toSet())
                return false
        }

        return true
    }

    private fun isCompletedLoadReceipts(): Boolean = receiptMap.value!!.size == receiptCount.get()


    private fun isCompletedLoadProducts(): Boolean {
        if (receiptMap.value!!.size != receiptCount.get())
            return false

        for (receipt in receiptMap.value!!.values) {
            if (receipt.productMap.isEmpty)
                return false
        }
        return receiptMap.value!!.isNotEmpty()
    }


    fun loadFinalTransferData() {
        //계좌 번호 로드
        CoroutineScope(Dispatchers.IO).launch {
            val receipts = receiptMap.value!!
            val finalCalculationMap = mutableMapOf<String, FinalTransferDTO>()

            for (receipt in receipts.values) {
                val payersId = receipt.payersId
                if (!finalCalculationMap.containsKey(payersId))
                    finalCalculationMap[payersId] = FinalTransferDTO(payersId, receipt.payersName, 0, 0, mutableListOf())

                var mySettlementAmount = finalCalculationMap[payersId]!!.transferMoney
                var totalMoney = finalCalculationMap[payersId]!!.totalMoney
                totalMoney += receipt.totalMoney

                for (product in receipt.productMap.values) {

                    for (participantId in product.participants.keys) {
                        if (participantId == myUid) {
                            mySettlementAmount += product.price / product.participants.size
                            break
                        }
                    }
                }

                if (payersId == myUid)
                    mySettlementAmount = -mySettlementAmount
                finalCalculationMap[payersId]!!.transferMoney = mySettlementAmount
                finalCalculationMap[payersId]!!.totalMoney = totalMoney
            }

            for (receipt in finalCalculationMap.values) {
                val payersId = receipt.payersId

                val bankAccountsResult = async {
                    bankAccountsRepository.getBankAccounts(payersId)
                }
                val bankAccounts = bankAccountsResult.await()
                finalCalculationMap[payersId]!!.accounts.addAll(bankAccounts)
            }

            withContext(Main) {
                finalTransferData.value = finalCalculationMap.values.toMutableList()
            }
        }
    }

    fun myCheckStatus() = verifiedParticipantIds.value!!.contains(myUid)

    fun completeCalculation() {
        CoroutineScope(Dispatchers.IO).launch {
            val myReceiptIds = mutableListOf<String>()
            for (receipt in receiptMap.value!!.values) {
                if (receipt.payersId == myUid) {
                    myReceiptIds.add(receipt.id)
                }
            }

            receiptRepository.modifyReceipts(mutableMapOf("status" to true), calcRoomId, myReceiptIds.toList())

            val isCompletedAllCalcResult = async {
                calcRoomRepository.isCompletedStatus(calcRoomId)
            }

            if (isCompletedAllCalcResult.await()) {
                calcRoomRepository.updateCalculationStatus(calcRoomId, false, receiptMap.value!!.keys.toMutableList())
                completedAllCalc.value = true
                // 정산이 완료되면 calcmainfragment에서 프래그먼트 전환
            }
        }
    }
}