package com.example.wapapp2.view.chat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.commons.interfaces.IAdapterItemCount

class ChatDataObserver(
    private val recycler: RecyclerView,
    private val manager: LinearLayoutManager,
    private val iAdapterItemCount: IAdapterItemCount,
    private val IsMessageMine: CheckMyMessage,
    private val newMessageReceivedCallback: NewMessageReceivedCallback
) : ListAdapterDataObserver(recycler, manager, iAdapterItemCount) {

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted_super(positionStart, itemCount)

        val count = iAdapterItemCount.getAdapterItemCount()
        val lastVisiblePosition =
            if (manager.reverseLayout) manager.findFirstCompletelyVisibleItemPosition()
            else manager.findLastCompletelyVisibleItemPosition()
        val loading = lastVisiblePosition == RecyclerView.NO_POSITION

        val atBottom = if (manager.reverseLayout)
            lastVisiblePosition == 0
        else
            positionStart >= count - 1 && lastVisiblePosition == positionStart - 1

        if (loading || atBottom)
            recycler.scrollToPosition(if (manager.reverseLayout) 0 else positionStart)

        else if(IsMessageMine.checkLastMessageMine())
            recycler.scrollToPosition(if (manager.reverseLayout) 0 else positionStart)

        else
            newMessageReceivedCallback.onReceived()

        super.onChangedList()
    }

    fun interface CheckMyMessage{
        fun checkLastMessageMine() : Boolean
    }

    fun interface NewMessageReceivedCallback{
        fun onReceived()
    }

}