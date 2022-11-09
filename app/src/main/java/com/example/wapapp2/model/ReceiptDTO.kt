package com.example.wapapp2.model

import android.net.Uri
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import org.joda.time.DateTime
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


/** 영수증
 * @param name : description of receipt
 */
data class ReceiptDTO(
        @Exclude
        val id: String,
        @ServerTimestamp
        @PropertyName("createdTime")
        val createdTime: Date? = null,
        @PropertyName("imgUrl")
        val imgUrl: String,
        @Exclude
        var imgUriInMyPhone: Uri?,
        @PropertyName("name")
        var name: String,
        @PropertyName("payersId")
        val payersId: String,
        @field:JvmField
        @PropertyName("status")
        val status: Boolean
) {
    @Exclude
    private val productList = ArrayList<ReceiptProductDTO>()

    @Exclude
    public var totalMoney = 0

    @Exclude
    public var myMoney = 0

    @Exclude
    public val date: String = DateTime.now().toString()

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