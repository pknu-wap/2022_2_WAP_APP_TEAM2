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
        onChangedList()
        val count = iAdapterItemCount.getAdapterItemCount()
        val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
        // If the recycler view is initially being loaded or the
        // user is at the bottom of the list, scroll to the bottom
        // of the list to show the newly added message.
        val loading = lastVisiblePosition == -1
        val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
        if (loading || atBottom) {
            recycler.scrollToPosition(positionStart)
        }
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
            if (iAdapterItemCount.getAdapterItemCount() > 0) {
                onSuccessful()
            } else {
                onFailed(emptyMsg!!)
            }
        }
    }
}