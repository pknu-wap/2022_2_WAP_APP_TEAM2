package com.example.wapapp2.commons.classes

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.commons.view.NewLoadingView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.paging.FirestorePagingAdapter

class ListAdapterDataObserver(
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

        onChangedList()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        onChangedList()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
    }

    private fun onChangedList() {
        loadingView?.apply {
            if (iAdapterItemCount.getAdapterItemCount() > 0)
                onSuccessful()
            else
                onFailed(emptyMsg!!)
        }
    }
}