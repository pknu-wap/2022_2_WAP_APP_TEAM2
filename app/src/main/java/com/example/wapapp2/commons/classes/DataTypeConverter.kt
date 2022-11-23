package com.example.wapapp2.commons.classes

import java.text.DecimalFormat

class DataTypeConverter {
    companion object {
        fun toKRW(value: Int): String {
            val format = DecimalFormat("#,###원")
            return format.format(value)
        }
    }
}