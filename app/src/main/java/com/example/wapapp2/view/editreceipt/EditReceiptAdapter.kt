package com.example.wapapp2.view.editreceipt

import android.annotation.SuppressLint
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.DelayTextWatcher
import com.example.wapapp2.databinding.ProductItemLayoutInNewCalcBinding
import com.example.wapapp2.model.ReceiptProductDTO

class EditReceiptAdapter(private val itemList: MutableList<ReceiptProductDTO> = mutableListOf(),
                         private val onUpdatedValueListener: EditReceiptFragment.OnUpdatedValueListener) : RecyclerView
.Adapter<EditReceiptAdapter
.CustomViewHolder>() {

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

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onViewAttachedToWindow(holder: CustomViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    class CustomViewHolder(private val binding: ProductItemLayoutInNewCalcBinding,
                           private val onUpdatedValueListener: EditReceiptFragment.OnUpdatedValueListener) :
            RecyclerView.ViewHolder(binding.root) {
        private var nameTextWatcher: DelayTextWatcher? = null
        private var priceTextWatcher: DelayTextWatcher? = null

        fun bind(receiptProductDTO: ReceiptProductDTO) {
            nameTextWatcher?.run {
                binding.calculationItemNameEditText.removeTextChangedListener(this)
                null
            }
            priceTextWatcher?.run {
                binding.productPriceEditText.removeTextChangedListener(this)
                null
            }

            nameTextWatcher = object : DelayTextWatcher() {
                override fun onFinalText(text: String) {
                    receiptProductDTO.name = text
                }
            }
            priceTextWatcher = object : DelayTextWatcher() {
                override fun onFinalText(text: String) {
                    receiptProductDTO.price = text.toInt()
                    onUpdatedValueListener.onUpdated()
                }
            }

            binding.productPriceEditText.addTextChangedListener(priceTextWatcher)
            binding.calculationItemNameEditText.addTextChangedListener(nameTextWatcher)

            binding.productPriceEditText.text = receiptProductDTO.price.toString().toEditable()
            binding.calculationItemNameEditText.text = receiptProductDTO.name.toEditable()
        }

        private fun String.toEditable(): Editable = Editable.Factory().newEditable(this)
    }
}