package com.example.wapapp2.viewmodel

import android.net.Uri
import androidx.core.util.lruCache
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.ReceiptImgRepositoryImpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import kotlinx.coroutines.*

class NewReceiptViewModel : ViewModel() {
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE
    private val receiptImgRepositoryImpl = ReceiptImgRepositoryImpl.INSTANCE
    private val receiptMap = HashMap<String, ReceiptDTO>()

    val removeReceiptLiveData: MutableLiveData<String> = MutableLiveData<String>()

    private val _addReceiptResult = MutableLiveData<Boolean>()
    val addReceiptResult get() = _addReceiptResult

    var calcRoomId: String? = null

    fun removeProduct(receiptId: String, receiptProductDTO: ReceiptProductDTO): Int {
        try {
            return receiptMap[receiptId]!!.removeProduct(receiptProductDTO)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun addReceipt(receiptDTO: ReceiptDTO, calcRoomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val addReceiptResult = receiptRepository.addReceipt(receiptDTO, calcRoomId)
            if (addReceiptResult) {
                //영수증 사진 있는 경우 추가
                receiptDTO.imgUriInMyPhone?.apply {
                    val addImgResult = receiptImgRepositoryImpl.uploadReceiptImg(this, calcRoomId)
                }

                val lastDocumentReference = receiptRepository.getLastDocumentReference(calcRoomId)
                lastDocumentReference?.apply {
                    val addProductsResult = receiptRepository.addProducts(this, receiptDTO.getProducts())
                    withContext(MainScope().coroutineContext) {
                        this@NewReceiptViewModel.addReceiptResult.value = addProductsResult
                    }
                }

                if (lastDocumentReference == null) {
                    withContext(MainScope().coroutineContext) {
                        this@NewReceiptViewModel.addReceiptResult.value = false
                    }
                }
            } else {
                withContext(MainScope().coroutineContext) {
                    this@NewReceiptViewModel.addReceiptResult.value = false
                }
            }
        }
    }

    fun addAllReceipts() {
        for (receipt in receiptMap.values) {
            addReceipt(receipt, calcRoomId!!)
        }
    }

    fun addReceipt(receiptId: String) {
        receiptMap[receiptId] = ReceiptDTO(receiptId, null, "", null, "", "0", false)
    }

    fun addProduct(receiptId: String): ReceiptProductDTO {
        val receiptProductDTO = ReceiptProductDTO("", "", 0, ArrayList<String>())
        receiptMap[receiptId]!!.addProduct(receiptProductDTO)
        return receiptProductDTO
    }

    fun removeReceipt(receiptId: String): Boolean {
        //영수증이 하나인 경우에는 삭제 불가 -> 수정해서 바꿔야 함
        return if (receiptMap.size == 1) {
            false
        } else {
            receiptMap.remove(receiptId)
            removeReceiptLiveData.value = receiptId
            true
        }
    }

    fun getReceipts(): MutableCollection<ReceiptDTO> = receiptMap.values

    fun getReceiptSet(): MutableSet<String> = receiptMap.keys

    fun getReceiptDTO(receiptId: String): ReceiptDTO? = receiptMap[receiptId]

    fun getProductCount(receiptId: String): Int = receiptMap[receiptId]?.getProducts()?.size ?: 0

    fun getReceiptCount(): Int = receiptMap.size

    fun calcTotalPrice(receiptId: String): String {
        var price = 0
        for (product in receiptMap[receiptId]!!.getProducts()) {
            price += product.price
        }

        receiptMap[receiptId]!!.totalMoney = price
        return price.toString()
    }

    fun calcTotalPrice(): String {
        var price = 0
        for (product in receiptMap.values) {
            price += product.totalMoney
        }

        return price.toString()
    }

    fun checkTotalMoneyWithProductsPrice(receiptId: String, totalPrice: Int): Boolean {
        var price = 0
        for (product in receiptMap[receiptId]!!.getProducts()) {
            price += product.price
        }

        return totalPrice == price
    }

}