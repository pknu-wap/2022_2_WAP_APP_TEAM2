package com.example.wapapp2.model

import org.joda.time.DateTime
import java.lang.Exception


/** 영수증
 * @param title : description of receipt
 */
data class ReceiptDTO(val id: String, var title: String) {
    private val productList = ArrayList<ReceiptProductDTO>()
    public var totalMoney = 0
    public var myMoney = 0
    public val date: String = DateTime.now().toString()

    init {

    }

    fun addProduct(receiptProductDTO: ReceiptProductDTO) {
        productList.add(receiptProductDTO)
        totalMoney += receiptProductDTO.price
    }

    fun getProducts(): ArrayList<ReceiptProductDTO> = productList


    fun removeProduct(receiptProductDTO: ReceiptProductDTO): Int {
        for ((index, value) in productList.withIndex()) {
            if (value == receiptProductDTO) {
                productList.removeAt(index)
                totalMoney -= receiptProductDTO.price

                return index
            }
        }
        throw Exception("no data")
    }

}