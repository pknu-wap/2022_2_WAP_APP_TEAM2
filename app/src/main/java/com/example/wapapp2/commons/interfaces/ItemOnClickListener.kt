package com.example.wapapp2.commons.interfaces


fun interface ItemOnClickListener<T> {
    fun onClicked(e: T)
}