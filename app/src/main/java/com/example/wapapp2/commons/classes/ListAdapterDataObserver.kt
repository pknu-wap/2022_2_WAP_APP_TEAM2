package com.example.wapapp2.commons.classes

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.commons.view.NewLoadingView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.paging.FirestorePagingAdapter

open class ListAdapterDataObserver(
        private val recycler: RecyclerView,
        private val manager: LinearLayoutManager,
        private val iAdapterItemCount: IAdapterItemCount,
) : RecyclerView.AdapterDataObserver() {
    private var loadingView: NewLoadingView? = null
    private var emptyMsg: String? = null


    fun registerLoadingView(loadingView: NewLoadingView, emptyMsg: String) {
        this.loadingView = loadingView
        this.emptyMsg = emptyMsg
    }

    override fun onChanged() {
        super.onChanged()
        onChangedList()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.onItemRangeChanged(positionStart, itemCount)
        onChangedList()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        super.onItemRangeChanged(positionStart, itemCount, payload)
        onChangedList()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)

        val loading = isLoading()
        val atBottom = atBottom(positionStart)

        if (loading || atBottom)
            scrollToBottom(positionStart)

        onChangedList()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        onChangedList()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
    }

    /**
     * 리스트가 비어있으면 메시지 표시하고 리스트 숨김
     * 비어있지 않으면 리스트 표시
     */
    protected fun onChangedList() {
        loadingView?.apply {
            if (iAdapterItemCount.getAdapterItemCount() > 0)
                onSuccessful()
            else
                onFailed(emptyMsg!!)
        }
    }


    fun atBottom(positionStart: Int): Boolean {
        val count = iAdapterItemCount.getAdapterItemCount()
        val lastVisiblePosition =
                if (manager.reverseLayout) manager.findFirstCompletelyVisibleItemPosition()
                else manager.findLastCompletelyVisibleItemPosition()

        return if (manager.reverseLayout)
            lastVisiblePosition == 0
        else
            positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
    }

    fun isLoading(): Boolean {
        val lastVisiblePosition =
                if (manager.reverseLayout) manager.findFirstCompletelyVisibleItemPosition()
                else manager.findLastCompletelyVisibleItemPosition()
        return lastVisiblePosition == RecyclerView.NO_POSITION
    }

    fun scrollToBottom(positionStart: Int) {
        recycler.scrollToPosition(if (manager.reverseLayout) 0 else positionStart)
    }


}