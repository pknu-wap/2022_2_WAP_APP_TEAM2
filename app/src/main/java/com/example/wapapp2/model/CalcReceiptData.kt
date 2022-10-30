package com.example.wapapp2.model

data class CalcReceiptData(val description : String, val menus : ArrayList<CalcReceiptMenuData>, val date : String, var summary : Int?) {
}