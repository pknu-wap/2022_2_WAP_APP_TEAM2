package com.example.wapapp2.view.receipt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.ReceiptProductInfoViewBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO

class ReceiptProductsAdapter : RecyclerView.Adapter<ReceiptProductsAdapter.ViewHolder>() {
    val modelList = mutableListOf<ReceiptProductDTO>()

    class ViewHolder(private val binding: ReceiptProductInfoViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ReceiptProductDTO) {
            binding.name.text = model.name
            binding.count.text = model.count.toString()

            val totalPrice = model.price * model.count
            val totalPriceTxt = "$totalPrice"
            binding.unitPrice.text = totalPriceTxt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(ReceiptProductInfoViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(modelList[position])
    }

    override fun getItemCount(): Int = modelList.size
}