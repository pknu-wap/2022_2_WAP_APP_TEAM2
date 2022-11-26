package com.example.wapapp2.view.chat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.model.ChatDTO

class ChatDataObserver(
        private val recycler: RecyclerView,
        private val manager: LinearLayoutManager,
        private val iAdapterItemCount: IAdapterItemCount,
        private val IsMessageMine: CheckMyMessage,
) : ListAdapterDataObserver(recycler, manager, iAdapterItemCount) {

    var newMessageReceivedCallback: NewMessageReceivedCallback? = null

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)

        // 내가 보낸 메시지이면 무조건 리스트 끝으로 이동
        if (IsMessageMine.checkLastMessageMine())
            scrollToBottom(positionStart)
        else {
            val atBottom = atBottom(positionStart)

            if (!atBottom)
            // 현재 스크롤 위치가 리스트 끝이 아니고, 받은 메시지이면 화면에 메시지 표시, 스크롤 이동X
                newMessageReceivedCallback?.onReceived()
        }
    }

    fun interface CheckMyMessage {
        fun checkLastMessageMine(): Boolean
    }

    fun interface NewMessageReceivedCallback {
        fun onReceived()
    }

}