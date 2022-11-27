package com.example.wapapp2.view.editreceipt

import android.annotation.SuppressLint
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.DelayTextWatcher
import com.example.wapapp2.databinding.ProductItemLayoutInNewCalcBinding
import com.example.wapapp2.model.ReceiptProductDTO

class EditReceiptAdapter(
        private val onUpdatedValueListener: EditReceiptFragment.OnUpdatedValueListener,
) : RecyclerView.Adapter<EditReceiptAdapter.CustomViewHolder>() {
    private val itemList: MutableList<ReceiptProductDTO> = mutableListOf()
    var items = itemList
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CustomViewHolder(ProductItemLayoutInNewCalcBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
    ), onUpdatedValueListener)

    override fun onBindViewHolder(holder: EditReceiptAdapter.CustomViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size


    class CustomViewHolder(
            private val binding: ProductItemLayoutInNewCalcBinding,
            private val onUpdatedValueListener: EditReceiptFragment.OnUpdatedValueListener,
    ) :
            RecyclerView.ViewHolder(binding.root) {
        private var nameTextWatcher: DelayTextWatcher? = null
        private var countTextWatcher: DelayTextWatcher? = null
        private var priceTextWatcher: DelayTextWatcher? = null

        fun bind(receiptProductDTO: ReceiptProductDTO) {
            nameTextWatcher?.run {
                binding.nameEditText.removeTextChangedListener(this)
                null
            }
            countTextWatcher?.run {
                binding.countEditText.removeTextChangedListener(this)
                null
            }
            priceTextWatcher?.run {
                binding.priceEditText.removeTextChangedListener(this)
                null
            }

            binding.priceEditText.text = receiptProductDTO.price.toString().toEditable()
            binding.countEditText.text = receiptProductDTO.count.toString().toEditable()
            binding.nameEditText.text = receiptProductDTO.name.toEditable()

            nameTextWatcher = object : DelayTextWatcher() {
                override fun onFinalText(text: String) {
                    receiptProductDTO.name = text
                }
            }
            countTextWatcher = object : DelayTextWatcher() {
                override fun onFinalText(text: String) {
                    if (isInt(text)) {
                        receiptProductDTO.count = text.toInt()
                    } else {
                        receiptProductDTO.count = 0
                        binding.countEditText.text = "1".toEditable()
                    }
                    onUpdatedValueListener.onUpdated()
                }
            }
            priceTextWatcher = object : DelayTextWatcher() {
                override fun onFinalText(text: String) {
                    if (isInt(text)) {
                        receiptProductDTO.price = text.toInt()
                    } else {
                        receiptProductDTO.price = 0
                        binding.priceEditText.text = "0".toEditable()
                    }
                    onUpdatedValueListener.onUpdated()
                }
            }

            binding.priceEditText.addTextChangedListener(priceTextWatcher)
            binding.countEditText.addTextChangedListener(countTextWatcher)
            binding.nameEditText.addTextChangedListener(nameTextWatcher)
        }

        private fun String.toEditable(): Editable = Editable.Factory().newEditable(this)
    }
}