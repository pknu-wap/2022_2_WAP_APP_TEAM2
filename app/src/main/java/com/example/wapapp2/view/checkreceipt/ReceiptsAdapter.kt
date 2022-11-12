package com.example.wapapp2.view.checkreceipt

import android.annotation.SuppressLint
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.CheckReceiptBinding
import com.example.wapapp2.model.ReceiptDTO
import java.text.SimpleDateFormat
import java.util.*

class ReceiptsAdapter(private val receiptList: MutableList<ReceiptDTO> = mutableListOf(), private val onClickListener:
ListOnClickListener<ReceiptDTO>) :
        RecyclerView.Adapter<ReceiptsAdapter.CustomViewHolder>() {

    var receipts = receiptList
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CustomViewHolder(CheckReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickListener)


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(receiptList[position])
    }

    override fun getItemCount(): Int {
        return receiptList.size
    }

    class CustomViewHolder(private val binding: CheckReceiptBinding, private val onClickListener:
    ListOnClickListener<ReceiptDTO>) : RecyclerView.ViewHolder(binding.root) {
        private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd E a hh:mm", Locale.getDefault())

        fun bind(receiptDTO: ReceiptDTO) {
            binding.receiptDate.text = simpleDateFormat.format(receiptDTO.createdTime!!)
            binding.receiptTitle.text = receiptDTO.name
            binding.receiptAmount.text = receiptDTO.totalMoney.toString().toEditable()

            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "금액 : ${receiptDTO.totalMoney}\n타이틀 : ${receiptDTO.name})", Toast
                        .LENGTH_SHORT
                ).show()

                onClickListener.onClicked(receiptDTO, adapterPosition)
            }

        }

        private fun String.toEditable(): Editable = Editable.Factory().newEditable(this)
    }
}