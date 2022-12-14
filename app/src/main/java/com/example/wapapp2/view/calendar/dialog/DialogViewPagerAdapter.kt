package com.example.wapapp2.view.calendar.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.CalendarDialogViewpagerItemBinding
import com.example.wapapp2.model.ReceiptDTO
import org.joda.time.DateTime

class DialogViewPagerAdapter(val hashMap: HashMap<String, ArrayList<ReceiptDTO>> ,beginDateText: String, context: Context, val receiptItemClickListener: ReceiptItemClickListener) : RecyclerView.Adapter<DialogViewPagerAdapter.ViewHolder>
() {
    private val layoutInflater: LayoutInflater
    private val dateFormat = "MM.dd E요일"
    private val beginDate: DateTime
    private val FIRST_VIEW_PAGER_POSITION = Int.MAX_VALUE / 2


    init {
        beginDate = DateTime.parse(beginDateText)
        layoutInflater = LayoutInflater.from(context)
    }

    inner class ViewHolder(private val binding: CalendarDialogViewpagerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var copiedDateTime: DateTime
        private lateinit var receiptListAdapter: ReceiptListForADayAdapter

        init {
            binding.receiptList.apply {
                addItemDecoration(DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL))
            }
        }

        fun bind(position: Int) {
            copiedDateTime = DateTime.parse(beginDate.toString())
            copiedDateTime = copiedDateTime.plusDays(position - FIRST_VIEW_PAGER_POSITION)

            receiptListAdapter = ReceiptListForADayAdapter(ArrayList (hashMap[copiedDateTime.toString("yyyyMMdd")] ?: listOf() ), receiptItemClickListener)
            binding.receiptList.adapter = receiptListAdapter


            binding.date.text = copiedDateTime.toString(dateFormat)
            binding.year.text = copiedDateTime.year.toString()

        }

        fun clear() {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CalendarDialogViewpagerItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.clear()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}