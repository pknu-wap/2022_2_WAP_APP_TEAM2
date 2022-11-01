package com.example.wapapp2.model

/** 영수증 세부 항목
 * @param itemName : 메뉴이름
 * @param price : 가격
 * @param personCount : 정산 참여 인원
 * **/
data class ReceiptProductDTO(var id : String, var itemName: String, var price: Int, var personCount : Int)