package com.example.wapapp2.view.friends.interfaces

import com.example.wapapp2.model.FriendDTO

fun interface OnRemovedFriendListener {
    fun onRemoved(friendDTO: FriendDTO)
}