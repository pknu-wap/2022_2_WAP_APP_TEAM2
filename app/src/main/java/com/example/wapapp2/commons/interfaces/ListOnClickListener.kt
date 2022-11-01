package com.example.wapapp2.commons.interfaces


fun interface ListOnClickListener<T> {
    fun onClicked(e: T, position: Int)
}