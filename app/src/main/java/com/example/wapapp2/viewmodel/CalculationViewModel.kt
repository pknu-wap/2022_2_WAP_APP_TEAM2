package com.example.wapapp2.viewmodel

import android.util.ArrayMap
import androidx.collection.arrayMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.CalcRoomRepositorylmpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.example.wapapp2.view.calculation.receipt.interfaces.IProductCheckBox
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class CalculationViewModel : ViewModel(), IProductCheckBox {
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE
    private val calcRoomRepository = CalcRoomRepositorylmpl.getINSTANCE()

    private var onGoingReceiptIdsListener: ListenerRegistration? = null

    //내 정산 금액
    val mySettlementAmount = MutableLiveData(0)
    val receiptMap = MutableLiveData(arrayMapOf<String, ReceiptDTO>())

    lateinit var calcRoomId: String

    override fun onCleared() {
        super.onCleared()
        onGoingReceiptIdsListener?.remove()
    }

    override fun updateSummaryForNewProduct(productDTO: ReceiptProductDTO) {
        val value = mySettlementAmount.value!! + try {
            productDTO.price / productDTO.personCount
        } catch (e: ArithmeticException) {
            0
        }

        mySettlementAmount.value = value
    }

    override fun onProductChecked(productDTO: ReceiptProductDTO) {
        val value = mySettlementAmount.value!! + productDTO.price / ++productDTO.personCount
        mySettlementAmount.value = value

        /*
        CoroutineScope(Dispatchers.IO).launch {
            receiptRepository.addMyID_fromProductParticipantIDs(productDTO.id)
        }
         */

    }

    override fun onProductUnchecked(productDTO: ReceiptProductDTO) {
        val value = mySettlementAmount.value!! - productDTO.price / productDTO.personCount--
        mySettlementAmount.value = value

        /*
        CoroutineScope(Dispatchers.IO).launch {
            receiptRepository.subMyID_fromProductParticipantIDs(productDTO.id)
        }
         */
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
            //영수증 항목 로드
            val productMap = arrayMapOf<String, MutableList<ReceiptProductDTO>>()
            val modifiedMap = receiptMap.value!!

            for (receipt in receiptList) {
                val productsResult = async {
                    receiptRepository.getProducts(receipt.id, calcRoomId)
                }
                productsResult.await()
                productMap[receipt.id] = productsResult.await()
                modifiedMap[receipt.id] = receipt
                modifiedMap[receipt.id]!!.getProducts().clear()
                modifiedMap[receipt.id]!!.getProducts().addAll(productMap[receipt.id]!!.toList())
            }

            withContext(Main) {
                receiptMap.value = modifiedMap
            }
        }
    }

}