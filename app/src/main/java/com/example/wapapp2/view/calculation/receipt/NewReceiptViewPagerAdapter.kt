package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class NewReceiptViewPagerAdapter(parentFragment: Fragment, private val list: ArrayList<Holder> = arrayListOf<Holder>()) :
        FragmentStateAdapter(parentFragment) {

    private var receiptId = 0
    private var pageIds = list.map { it.hashCode().toLong() }

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment = list[position].fragment

    fun addFragment(): NewReceiptItemFragment {
        val fragment = NewReceiptItemFragment()
        fragment.arguments = Bundle().also {
            it.putString("receiptId", receiptId.toString())
        }

        list.add(Holder(receiptId.toString(), fragment))
        pageIds = list.map { it.hashCode().toLong() }

        receiptId++

        notifyItemInserted(list.size - 1)
        return fragment
    }

    fun remove(receiptId: String): Boolean {
        for ((index, value) in list.withIndex()) {
            if (value.receiptId == receiptId) {
                list.removeAt(index)
                pageIds = list.map { it.hashCode().toLong() }
                notifyItemRangeChanged(index, list.size)
                notifyDataSetChanged()
                return true
            }
        }
        return false
    }

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong() // make sure notifyDataSetChanged() works
    }

    override fun containsItem(itemId: Long): Boolean {
        return pageIds.contains(itemId)
    }

    fun getTotalMoney(receiptId: String): String {
        for (holder in list) {
            if (holder.receiptId == receiptId) {
                return holder.fragment.getTotalMoney()
            }
        }
        return ""
    }

    data class Holder(val receiptId: String, val fragment: NewReceiptItemFragment)
}