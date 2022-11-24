package com.example.wapapp2.view.receipt

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.DataTypeConverter
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.ReceiptProductInfoViewBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.model.ReceiptProductParticipantDTO
import okhttp3.internal.notify

class ReceiptProductsAdapter(
        private val productOnClickListener: ListOnClickListener<ReceiptProductDTO>,
        private val participantOnClickListener: ListOnClickListener<ReceiptProductParticipantDTO>,
) : RecyclerView
.Adapter<ReceiptProductsAdapter
.ViewHolder>() {
    val modelList = mutableListOf<ReceiptProductDTO>()

    class ViewHolder(
            private val binding: ReceiptProductInfoViewBinding,
            private val productOnClickListener: ListOnClickListener<ReceiptProductDTO>,
            private val participantOnClickListener: ListOnClickListener<ReceiptProductParticipantDTO>,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val participantsAdapter: ProductParticipantsAdapter

        init {
            binding.participants.setHasFixedSize(true)
            participantsAdapter = ProductParticipantsAdapter(participantOnClickListener)
            binding.participants.adapter = participantsAdapter
        }

        fun bind(model: ReceiptProductDTO) {
            binding.name.text = model.name
            binding.count.text = model.count.toString()

            val totalPrice = model.price * model.count
            binding.unitPrice.text = DataTypeConverter.toKRW(model.price)
            binding.totalPrice.text = DataTypeConverter.toKRW(totalPrice)

            binding.root.setOnClickListener {
                productOnClickListener.onClicked(model, bindingAdapterPosition)
            }

            createParticipants(model)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun createParticipants(model: ReceiptProductDTO) {
            participantsAdapter.participants.clear()
            participantsAdapter.participants.addAll(model.participants.toMutableList())
            binding.participants.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ReceiptProductInfoViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), productOnClickListener, participantOnClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(modelList[position])
    }

    override fun getItemCount(): Int = modelList.size
}