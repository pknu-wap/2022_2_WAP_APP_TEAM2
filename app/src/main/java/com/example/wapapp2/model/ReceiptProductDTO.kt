package com.example.wapapp2.model

import com.google.firebase.firestore.Exclude

/** 영수증 세부 항목
 * @param name : 메뉴이름
 * @param price : 가격
 * @param checkedUserIds : 확인한 인원id
 * **/
data class ReceiptProductDTO(
        @Exclude
        var id: String,
        var name: String,
        var price: Int,
        var checkedUserIds: ArrayList<String> = ArrayList<String>())
