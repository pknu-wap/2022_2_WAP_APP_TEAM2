package com.example.wapapp2.view.friends.interfaces

import com.example.wapapp2.model.FriendDTO

fun interface OnCheckedFriendListener {
    fun onChecked(isChecked: Boolean, friendDTO: FriendDTO)
}