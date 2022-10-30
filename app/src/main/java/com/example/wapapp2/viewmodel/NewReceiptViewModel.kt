package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.calculation.receipt.NewReceiptFragment

class NewReceiptViewModel(application: Application) : AndroidViewModel(application) {
    private val receiptMap = HashMap<String, ReceiptDTO>()

    val removeReceiptLiveData: MutableLiveData<String> = MutableLiveData<String>()

    fun removeProduct(receiptId: String, receiptProductDTO: ReceiptProductDTO): Int {
        try {
            return receiptMap[receiptId]!!.removeProduct(receiptProductDTO)
        } catch (e: Exception) {
            throw e
        }
    }

    fun addReceipt(receiptId: String) {
        receiptMap[receiptId] = ReceiptDTO(receiptId, "")
    }

    fun addProduct(receiptId: String): ReceiptProductDTO {
        val receiptProductDTO = ReceiptProductDTO("",  0, 0)
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