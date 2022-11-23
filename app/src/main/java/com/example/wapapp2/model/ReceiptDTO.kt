package com.example.wapapp2.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


/** 영수증
 * @param name : description of receipt
 */
@Parcelize
data class ReceiptDTO(
        @get:Exclude
        var id: String,
        @ServerTimestamp
        @PropertyName("createdTime")
        var createdTime: Date? = null,
        @PropertyName("imgUrl")
        var imgUrl: String?,
        @get:Exclude
        var imgUriInMyPhone: Uri?,
        @PropertyName("name")
        var name: String,
        @PropertyName("payersId")
        var payersId: String,
        @field:JvmField
        @PropertyName("status")
        var status: Boolean,

        @PropertyName("totalMoney")
        var totalMoney: Int = 0,

        @get:Exclude
        private val productList: ArrayList<ReceiptProductDTO>,

        @get:Exclude
        public var myMoney: Int = 0,

        @get:Exclude
        public val date: DateTime = DateTime.now(),

        ) : Parcelable {
    constructor() : this("", null, "", null, "", "", false, 0, arrayListOf(), 0, DateTime.now())

    fun addProduct(receiptProductDTO: ReceiptProductDTO) {
        productList.add(receiptProductDTO)
        totalMoney += receiptProductDTO.price
    }
    var roomID : String? = null


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