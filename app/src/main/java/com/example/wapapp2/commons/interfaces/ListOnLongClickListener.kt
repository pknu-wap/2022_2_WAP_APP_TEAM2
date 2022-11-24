package com.example.wapapp2.commons.interfaces


fun interface ListOnLongClickListener<T> {
    fun onLongClicked(e: T, position: Int)
}