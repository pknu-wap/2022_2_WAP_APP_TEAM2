package com.example.wapapp2.model

import java.lang.Exception

data class ReceiptDTO(val menu: String, var totalMoney: Int, var myMoney: Int, var personCount: Int) {
    private val productList = ArrayList<ReceiptProductDTO>()

    fun addProduct(receiptProductDTO: ReceiptProductDTO) {
        productList.add(receiptProductDTO)
    }

    fun getProducts(): ArrayList<ReceiptProductDTO> = productList

    fun removeProduct(receiptProductDTO: ReceiptProductDTO): Int {
        for ((index, value) in productList.withIndex()) {
            if (value == receiptProductDTO) {
                productList.removeAt(index)
                return index
            }
        }
        throw Exception("no data")
    }

}