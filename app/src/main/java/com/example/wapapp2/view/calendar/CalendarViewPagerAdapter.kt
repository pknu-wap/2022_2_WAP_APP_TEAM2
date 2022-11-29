package com.example.wapapp2.view.calendar

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.CalendarViewpagerItemBinding

class CalendarViewPagerAdapter : RecyclerView.Adapter<CalendarViewPagerAdapter.CalendarViewHolder>() {
    inner class CalendarViewHolder(private val binding : CalendarViewpagerItemBinding) : RecyclerView.ViewHolder(binding.root){
/*
        fun bind(position: Int){
            binding.calendarDate.text = dstDate.toString("yyyy년 MM월")
            binding.calendarRv.adapter = CalendarAdapter(dstDate.withDayOfMonth(1), hashMap!! ,dayItemOnClickListener)


            updateCal(myCalendarViewModel.myReceiptMap ?: hashMapOf())

            myCalendarViewModel.myReceiptMapLivedata.observe(viewLifecycleOwner){
                updateCal(myCalendarViewModel.myReceiptMap!!)
            }

            //unscrollable

            binding.calendarBtnBack.setOnClickListener(View.OnClickListener {
                dstDate = dstDate.minusMonths(1)
                updateCal(myCalendarViewModel.myReceiptMap ?: hashMapOf())
            })
            binding.calendarBtnNext.setOnClickListener(View.OnClickListener {
                dstDate = dstDate.plusMonths(1)
                updateCal(myCalendarViewModel.myReceiptMap ?: hashMapOf())
            })
        }

        fun init(){
            binding.calendarRv.layoutManager = object : GridLayoutManager(context,7) { override fun canScrollVertically() = false }


 */
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}