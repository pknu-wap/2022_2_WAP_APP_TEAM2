package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.ReceiptRepository

class ReceiptViewModel(application : Application) :AndroidViewModel(application) {
    private val receiptRepository = ReceiptRepository.getINSTANCE()

    fun updateSummary_forNewProduct(productDTO : ReceiptProductDTO){
        receiptRepository.currentMySummary += try{productDTO.price / productDTO.personCount} catch (e : ArithmeticException) {0}
    }

    fun product_checked(productDTO : ReceiptProductDTO){
        receiptRepository.currentMySummary += productDTO.price / ++productDTO.personCount
        receiptRepository.addMyID_fromProductParticipantIDs(productDTO.id)
    }

    fun product_unchecked(productDTO : ReceiptProductDTO){
        receiptRepository.currentMySummary -= productDTO.price / productDTO.personCount--
        receiptRepository.subMyID_fromProductParticipantIDs(productDTO.id)
    }

    fun getCurrentSummary() : Int{
        return receiptRepository.currentMySummary
    }

}