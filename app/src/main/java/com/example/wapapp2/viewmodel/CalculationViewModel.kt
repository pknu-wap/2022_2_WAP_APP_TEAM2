package com.example.wapapp2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.example.wapapp2.view.calculation.receipt.interfaces.IProductCheckBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalculationViewModel : ViewModel(), IProductCheckBox {
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE

    //내 정산 금액
    val mySettlementAmount = MutableLiveData(0)

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

}