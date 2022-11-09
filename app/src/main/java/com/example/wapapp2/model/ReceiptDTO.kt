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
        @get:Exclude
        var id: String,
        @ServerTimestamp
        @PropertyName("createdTime")
        var createdTime: Date? = null,
        @PropertyName("imgUrl")
        var imgUrl: String,
        @get:Exclude
        var imgUriInMyPhone: Uri?,
        @PropertyName("name")
        var name: String,
        @PropertyName("payersId")
        var payersId: String,
        @field:JvmField
        @PropertyName("status")
        var status: Boolean
) {
    @get:Exclude
    public var totalMoney = 0

    @get:Exclude
    private val productList = ArrayList<ReceiptProductDTO>()

    @get:Exclude
    public var myMoney = 0

    @get:Exclude
    public val date: String = DateTime.now().toString()

    fun addProduct(receiptProductDTO: ReceiptProductDTO) {
        productList.add(receiptProductDTO)
        totalMoney += receiptProductDTO.price
    }

    @Exclude
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