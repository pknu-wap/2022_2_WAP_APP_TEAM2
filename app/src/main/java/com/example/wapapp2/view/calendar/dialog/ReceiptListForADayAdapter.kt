package com.example.wapapp2.view.calendar.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.DialogCalculationItemBinding
import com.example.wapapp2.model.ReceiptDTO
import org.joda.time.DateTime

class ReceiptListForADayAdapter(private val list: ArrayList<ReceiptDTO>) : RecyclerView.Adapter<ReceiptListForADayAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: DialogCalculationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val dstReceipt = list[position]
            binding.receiptName.text = dstReceipt.name
            binding.time.text = DateTime.parse(dstReceipt.date.toString()).toString("aa hh:mm")
            binding.totalMoney.text = dstReceipt.totalMoney.toString()

            // 정산진행상태/ 등등 구현 필요
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DialogCalculationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = list.size
}
