package com.example.wapapp2.view.calculation

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.commons.view.NewLoadingView

class CalcRoomDataObserver(
    private val recycler: RecyclerView,
    private val manager: LinearLayoutManager,
    private val iAdapterItemCount: IAdapterItemCount,
) : ListAdapterDataObserver(recycler, manager, iAdapterItemCount) {


    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        if (isLoading())
            scrollToTop()

    }

}