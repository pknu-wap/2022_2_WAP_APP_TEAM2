package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.example.wapapp2.repository.interfaces.ReceiptRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ReceiptViewModel(application: Application) : AndroidViewModel(application) {
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE
    private var currentMySummary = 0

    val getCurrentSummary get() = currentMySummary

    fun updateSummary_forNewProduct(productDTO: ReceiptProductDTO) {
        currentMySummary += try {
            productDTO.price / productDTO.personCount
        } catch (e: ArithmeticException) {
            0
        }
    }

    fun product_checked(productDTO: ReceiptProductDTO) {
        currentMySummary += productDTO.price / ++productDTO.personCount
        CoroutineScope(Dispatchers.Default).launch {
            receiptRepository.addMyID_fromProductParticipantIDs(productDTO.id)
        }
    }

    fun product_unchecked(productDTO: ReceiptProductDTO) {
        currentMySummary -= productDTO.price / productDTO.personCount--
        CoroutineScope(Dispatchers.Default).launch {
            receiptRepository.subMyID_fromProductParticipantIDs(productDTO.id)
        }
    }

    fun getCurrentSummary(): Int {
        return currentMySummary
    }

}