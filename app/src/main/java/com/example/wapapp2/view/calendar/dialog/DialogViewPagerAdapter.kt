package com.example.wapapp2.view.calendar.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.CalendarDialogViewpagerItemBinding
import org.joda.time.DateTime
import java.util.*

class DialogViewPagerAdapter(beginDateText: String, context: Context) : RecyclerView.Adapter<DialogViewPagerAdapter.ViewHolder>
() {
    private val layoutInflater: LayoutInflater
    private val dateTimeFormat = "yyyy.MM.dd E"
    private val beginDate: DateTime
    private val FIRST_VIEW_PAGER_POSITION = Int.MAX_VALUE / 2


    init {
        beginDate = DateTime.parse(beginDateText)
        layoutInflater = LayoutInflater.from(context)
    }

    inner class ViewHolder(private val binding: CalendarDialogViewpagerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var copiedDateTime: DateTime
        fun bind() {
            val position = adapterPosition

            copiedDateTime = DateTime.parse(beginDate.toString())
            copiedDateTime = copiedDateTime.plusDays(position - FIRST_VIEW_PAGER_POSITION)

            binding.date.text = copiedDateTime.toString(dateTimeFormat)
        }

        fun clear() {
            binding.date.text = "error"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CalendarDialogViewpagerItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.clear()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}