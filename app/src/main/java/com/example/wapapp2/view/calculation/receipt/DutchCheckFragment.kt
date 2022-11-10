package com.example.wapapp2.view.calculation.receipt

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.DutchCheckFragmentBinding
import com.example.wapapp2.databinding.ViewReceiptItemBinding
import com.example.wapapp2.databinding.ViewRecentCalcItemBinding
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.view.calculation.interfaces.OnFixOngoingCallback
import com.example.wapapp2.view.calculation.interfaces.OnUpdateSummaryCallback
import com.example.wapapp2.viewmodel.ReceiptViewModel
import org.joda.time.DateTime
import java.text.DecimalFormat

class DutchCheckFragment(onFixOngoingReceipt: OnFixOngoingCallback, onUpdateSummaryCallback: OnUpdateSummaryCallback) : Fragment() {
    private lateinit var binding : DutchCheckFragmentBinding
    val onFixOngoingReceipt : OnFixOngoingCallback
    val onUpdateSummaryCallback : OnUpdateSummaryCallback
    val receiptViewModel : ReceiptViewModel by viewModels()

    init {
        this.onFixOngoingReceipt = onFixOngoingReceipt
        this.onUpdateSummaryCallback = onUpdateSummaryCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DutchCheckFragmentBinding.inflate(inflater)

        binding.viewReceipts.adapter = ReceiptAdapter(requireContext(), DummyData.getReceipts())

        binding.btnDone.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked){

            }
            else{
                //임시로 구현 -> 팀 인원 다 체크후 해야함
                onFixOngoingReceipt.onFixOngoingReceipt()
            }
        })

        return binding.root
    }

    /** 영수증 Adapter **/
    private inner class ReceiptAdapter(private val context: Context?, private val receipts: ArrayList<ReceiptDTO>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        inner class ReceiptVM(val binding: ViewReceiptItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(receipt: ReceiptDTO) {
                binding.description.text = "[ " + receipt.title + " ] - 김진우"
                binding.recentCalcItem.adapter = ReceiptItemAdapter(context, receipt.getProducts())
                binding.dateTime.text = DateTime.parse(receipt.date).toString("yyyy-MM-dd")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ReceiptVM(ViewReceiptItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return (holder as ReceiptVM).bind(receipts[position])
        }

        override fun getItemCount(): Int {
            return receipts.size
        }

    }

    /** 영수증 세부 항목 Adapter **/
    private inner class ReceiptItemAdapter(private val context: Context?, private val items: ArrayList<ReceiptProductDTO>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        inner class ReceiptMenuVH(val binding: ViewRecentCalcItemBinding) : RecyclerView.ViewHolder(binding.root) {


            fun bind(product: ReceiptProductDTO) {
                var myPrice = try{product.price / product.personCount} catch (e : ArithmeticException) {0}

                binding.receiptMenu.text = product.itemName
                binding.receiptTotalMoney.text = DecimalFormat("#,###").format(product.price)
                binding.receiptMyMoney.text =  DecimalFormat("#,###").format(myPrice)
                binding.receiptPersonCount.text = "${product.personCount}/3"
                binding.recentCalcCkbox.isChecked = true

                receiptViewModel.updateSummary_forNewProduct(product)

                binding.recentCalcCkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        receiptViewModel.product_checked(product)
                        binding.receiptMyMoney.text = DecimalFormat("#,###").format(myPrice)
                        binding.receiptPersonCount.text = product.personCount.toString() + "/3"
                        onUpdateSummaryCallback.updateSummaryUI()
                    } else {
                        receiptViewModel.product_unchecked(product)
                        binding.receiptMyMoney.text = "0"
                        binding.receiptPersonCount.text = product.personCount.toString() + "/3"
                        onUpdateSummaryCallback.updateSummaryUI()
                    }

                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ReceiptMenuVH(ViewRecentCalcItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return (holder as ReceiptMenuVH).bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }


}