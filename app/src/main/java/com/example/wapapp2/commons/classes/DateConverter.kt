package com.example.wapapp2.commons.classes

import java.text.SimpleDateFormat
import java.util.*

class DateConverter private constructor() {
    companion object {
        fun toISO8601(date: Date): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            return sdf.format(date)
        }
    }
}