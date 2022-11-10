package com.example.wapapp2.repository

import android.content.Context

class ReceiptRepository private constructor(){
    var currentMySummary = 0

    companion object {
        private lateinit var INSTANCE: ReceiptRepository

        fun getINSTANCE() = INSTANCE

        fun initialize() {
            INSTANCE = ReceiptRepository()
        }
    }


    /** receipt_product 컬렉션의 참여 유저 id에서 자기 아이디 추가
     * when : checked
     * **/
    fun addMyID_fromProductParticipantIDs(product_id : String){

    }

    /** receipt_product 컬렉션의 참여 유저 id에서 자기 아이디 제외
     * when : unchecked
     * **/
    fun subMyID_fromProductParticipantIDs(product_id : String){

    }
}
