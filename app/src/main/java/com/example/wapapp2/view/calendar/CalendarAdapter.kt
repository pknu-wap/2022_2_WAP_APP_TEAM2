package com.example.wapapp2.view.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.CalendarDayBinding
import com.example.wapapp2.databinding.CalendarDayMarkingBinding
import com.example.wapapp2.model.ReceiptDTO
import org.joda.time.DateTime


class CalendarAdapter(val firstDate_inDstMonth : DateTime, val hashMapOfReceipts : HashMap<String, ArrayList<ReceiptDTO>>, val dayItemOnClickListener : ListOnClickListener<String>)
    : RecyclerView.Adapter<CalendarAdapter.ViewHolder>(), IAdapterItemCount {

    private lateinit var item_binding: CalendarDayBinding

    private val ROW = 7
    private val COL = 5

    val dayStart = firstDate_inDstMonth.minusDays(firstDate_inDstMonth.dayOfWeek%7)
    val dstMonth = firstDate_inDstMonth.monthOfYear


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pos: Int) {
            val dstDate = dayStart.plusDays(pos)
            item_binding.calendarDay.text = dstDate.dayOfMonth.toString()
            if(dstDate.monthOfYear != dstMonth){ item_binding.calendarDay.setTextColor(Color.LTGRAY) }
            item_binding.dayMarking.adapter = DayMarkingItemAdapter(dstDate)
            item_binding.root.setOnClickListener {
                dayItemOnClickListener.onClicked(dstDate.toString(), pos)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        item_binding = CalendarDayBinding.inflate(LayoutInflater.from(parent.context))
        item_binding.root.minimumHeight = parent.measuredHeight / ROW
        return ViewHolder(item_binding.root)
    }


    override fun getItemCount(): Int {
        return ROW * COL
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


    fun getPositionForDate(dstDate: DateTime) : Int{
         return dstDate.minus(dayStart.millis).dayOfMonth
    }



    inner class DayMarkingItemAdapter(val dstDate : DateTime) : RecyclerView.Adapter<DayMarkingItemAdapter.DayMarkingViewHolder>(){
        private lateinit var inner_item_binding: CalendarDayMarkingBinding
        private val markingItems : List<ReceiptDTO> = hashMapOfReceipts[dstDate.toString("yyyyMMdd")] ?: listOf()

        inner class DayMarkingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            fun bind(pos : Int){
                inner_item_binding.marking.text = markingItems!![pos].name
                //inner_item_binding.marking.setBackgroundColor(if (markingItems!![pos].status) 0x333333 else 0x000000)
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayMarkingViewHolder {
            inner_item_binding = CalendarDayMarkingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            /** Click disable 구현 **/
            inner_item_binding.root.setOnClickListener(View.OnClickListener { dayItemOnClickListener.onClicked(dstDate.toString(),-1) })
            return DayMarkingViewHolder(inner_item_binding.root)
        }

        override fun onBindViewHolder(holder: DayMarkingViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int {
            return if(markingItems.size <= 4) markingItems.size else 4
        }

    }

    override fun getAdapterItemCount() = itemCount
}