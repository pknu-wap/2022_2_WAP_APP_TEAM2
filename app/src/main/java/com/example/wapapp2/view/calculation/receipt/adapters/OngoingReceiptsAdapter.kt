package com.example.wapapp2.view.calculation.receipt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.DataTypeConverter
import com.example.wapapp2.commons.classes.DelayCheckBoxListener
import com.example.wapapp2.databinding.ViewReceiptItemBinding
import com.example.wapapp2.databinding.ViewRecentCalcProductBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.view.calculation.receipt.interfaces.IProductCheckBox
import java.text.SimpleDateFormat
import java.util.*

/** 영수증 Adapter **/
class OngoingReceiptsAdapter(
        private val iProductCheckBox: IProductCheckBox,
) : RecyclerView.Adapter<OngoingReceiptsAdapter.ReceiptVM>() {
    val receiptMap = arrayMapOf<String, ReceiptDTO>()

    companion object {
        var PARTICIPANT_COUNT = 0
        lateinit var MY_UID: String
    }

    class ReceiptVM(
            private val binding: ViewReceiptItemBinding,
            private val iProductCheckBox: IProductCheckBox,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var productsAdapter: ProductsAdapter? = null
        private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd E a hh:mm", Locale.getDefault())

        init {
            binding.recentCalcItem.addItemDecoration(DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL))
        }

        fun bind(receipt: ReceiptDTO) {
            val description = "${receipt.name} - ${receipt.payersName}"
            binding.description.text = description

            productsAdapter = null
            productsAdapter = ProductsAdapter(receipt.productMap, iProductCheckBox)
            binding.recentCalcItem.adapter = productsAdapter
            binding.dateTime.text = dateTimeFormat.format(receipt.createdTime!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptVM =
            ReceiptVM(ViewReceiptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), iProductCheckBox)

    override fun onBindViewHolder(holder: ReceiptVM, position: Int) {
        holder.bind(receiptMap.valueAt(position))
    }

    override fun getItemCount(): Int = receiptMap.size

    /** 영수증 세부 항목 Adapter **/
    private class ProductsAdapter(
            private val items: ArrayMap<String, ReceiptProductDTO>,
            private val iProductCheckBox: IProductCheckBox,
    ) : RecyclerView.Adapter<ProductsAdapter.ProductVH>() {

        class ProductVH(
                private val binding: ViewRecentCalcProductBinding,
                private val iProductCheckBox: IProductCheckBox,
        ) :
                RecyclerView.ViewHolder(binding.root) {
            private var delayCheckBoxListener: DelayCheckBoxListener? = null

            fun bind(product: ReceiptProductDTO) {
                binding.receiptMenu.text = product.name
                binding.receiptTotalMoney.text = DataTypeConverter.toKRW(product.price)
                binding.receiptMyMoney.text = DataTypeConverter.toKRW(calcMyMoney(product))
                binding.recentCalcCkbox.isChecked = product.participants.containsKey(MY_UID)

                iProductCheckBox.updateSummaryForNewProduct(product)

                delayCheckBoxListener = object : DelayCheckBoxListener(4000L) {
                    override fun onCheckedChanged(isChecked: Boolean) {
                        if (isChecked) {
                            iProductCheckBox.onProductChecked(product)
                            val newMoney = calcMyMoney(product)
                            binding.receiptMyMoney.text = DataTypeConverter.toKRW(newMoney)
                        } else {
                            iProductCheckBox.onProductUnchecked(product)
                            binding.receiptMyMoney.text = DataTypeConverter.toKRW(0)
                        }

                        val numOfPeopleSelected = "${product.numOfPeopleSelected}/${PARTICIPANT_COUNT}"
                        binding.receiptNumOfPeopleSelected.text = numOfPeopleSelected
                    }
                }

                binding.recentCalcCkbox.addOnCheckedStateChangedListener(delayCheckBoxListener!!)

                val numOfPeopleSelected = "${product.numOfPeopleSelected}/${PARTICIPANT_COUNT}"
                binding.receiptNumOfPeopleSelected.text = numOfPeopleSelected

            }

            private fun calcMyMoney(product: ReceiptProductDTO): Int {
                return try {
                    product.price / product.numOfPeopleSelected
                } catch (e: ArithmeticException) {
                    0
                }
            }

        }

        override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int,
        ): ProductVH = ProductVH(ViewRecentCalcProductBinding.inflate(LayoutInflater.from(parent.context), parent, false), iProductCheckBox)

        override fun onBindViewHolder(holder: ProductVH, position: Int) {
            holder.bind(items.valueAt(position))
        }

        override fun getItemCount(): Int = items.size
    }

}

