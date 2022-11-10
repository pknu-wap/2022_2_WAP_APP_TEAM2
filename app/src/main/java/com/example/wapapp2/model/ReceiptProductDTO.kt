package com.example.wapapp2.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

/** 영수증 세부 항목
 * @param name : 메뉴이름
 * @param price : 가격
 * @param checkedUserIds : 확인한 인원id
 * **/
@Parcelize
data class ReceiptProductDTO(
        @get:Exclude
        var id: String,
        @PropertyName("name")
        var name: String,
        @PropertyName("price")
        var price: Int,
        @PropertyName("checkedUserIds")
        var checkedUserIds: ArrayList<String> = ArrayList<String>(),
        @get:Exclude
        var personCount: Int = 0
) : Parcelable {
    constructor() : this("", "", 0, arrayListOf(), 0)
}
