package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.example.wapapp2.repository.interfaces.ReceiptRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReceiptViewModel(application: Application) : AndroidViewModel(application) {
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE
    private var currentMySummary = 0

    val getCurrentSummary get() = currentMySummary

    private val _receipts = MutableLiveData<MutableList<ReceiptDTO>>()
    val receipts get() = _receipts

    private val _products = MutableLiveData<MutableList<ReceiptProductDTO>>()
    val products get() = _products

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


    fun getReceipts(calcRoomId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                receiptRepository.getReceipts(calcRoomId)
            }
            result.await()
            withContext(Main) {
                receipts.value = result.await()
            }
        }
    }

    fun getProducts(receiptId: String, calcRoomId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                receiptRepository.getProducts(receiptId, calcRoomId)
            }
            result.await()
            withContext(Main) {
                products.value = result.await()
            }
        }
    }

    fun calcTotalPrice(): String {
        var price = 0
        for (product in products.value!!) {
            price += product.price
        }

        return price.toString()
    }

}