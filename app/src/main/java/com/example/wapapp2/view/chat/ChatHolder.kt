package com.example.wapapp2.view.chat

import com.example.wapapp2.model.ChatDTO

fun interface ChatHolder {
    fun bind(position: Int, model : ChatDTO)
}