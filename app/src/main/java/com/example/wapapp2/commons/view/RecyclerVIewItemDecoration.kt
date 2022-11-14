package com.example.wapapp2.commons.view

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.annotations.NotNull

class RecyclerViewItemDecoration : RecyclerView.ItemDecoration {
    val marginHorizontal: Int
    val marginVertical: Int
    val hasFab: Boolean
    val bottomFreeSpace: Int

    constructor(context: Context, hasFab: Boolean, fabCenterHeight: Int) {
        marginHorizontal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                16f, context.resources.displayMetrics).toInt()
        marginVertical = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2f, context.resources.displayMetrics).toInt()
        this.hasFab = hasFab
        this.bottomFreeSpace = fabCenterHeight * 2
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildLayoutPosition(view)
        val itemCounts = parent.adapter!!.itemCount

        //set right margin to all
        outRect.right = marginHorizontal;
        outRect.left = marginHorizontal;
        //set bottom margin to all
        //we only add top margin to the first row
        if (position < itemCounts) {
            outRect.top = marginVertical
        }

        if (hasFab && position == itemCounts - 1) {
            outRect.bottom = marginVertical + bottomFreeSpace
        } else {
            outRect.bottom = marginVertical
        }

    }
}