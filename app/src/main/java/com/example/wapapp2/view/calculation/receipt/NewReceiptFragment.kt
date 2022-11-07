package com.example.wapapp2.view.calculation.receipt

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FinalConfirmationNewReceiptLayoutBinding
import com.example.wapapp2.databinding.FragmentNewReceiptBinding
import com.example.wapapp2.databinding.ReceiptProductViewBinding
import com.example.wapapp2.databinding.SummaryReceiptViewBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.observer.MyLifeCycleObserver
import com.example.wapapp2.viewmodel.NewReceiptViewModel


open class NewReceiptFragment : Fragment() {
    private lateinit var binding: FragmentNewReceiptBinding
    private val newReceiptViewModel: NewReceiptViewModel by viewModels()
    private lateinit var adapter: NewReceiptViewPagerAdapter
    private lateinit var myObserver: DefaultLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = NewReceiptViewPagerAdapter(this)
        myObserver = MyLifeCycleObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(myObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewReceiptBinding.inflate(inflater)
        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.viewPager.also {

            it.adapter = adapter
            val indicator = binding.indicator
            it.adapter!!.registerAdapterDataObserver(indicator.adapterDataObserver)
            indicator.setViewPager(it)
        }

        binding.saveBtn.setOnClickListener {
            val confirmReceiptsDialogFragment = ConfirmReceiptsDialogFragment()
            confirmReceiptsDialogFragment.show(childFragmentManager, "confirm")
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addReceiptItem.setOnClickListener {
            adapter.addFragment()
            binding.viewPager.setCurrentItem(adapter.itemCount - 1, true)
            binding.receiptCount.text = adapter.itemCount.toString()
        }

        newReceiptViewModel.removeReceiptLiveData.observe(viewLifecycleOwner) {
            it?.apply {
                adapter.remove(it)
                binding.viewPager.setCurrentItem(adapter.itemCount - 1, true)
                binding.receiptCount.text = adapter.itemCount.toString()
            }
        }

        adapter.addFragment()
    }

    class ConfirmReceiptsDialogFragment : DialogFragment() {
        private lateinit var confirmBinding: FinalConfirmationNewReceiptLayoutBinding
        private var adapter: ReceiptsAdapter? = null
        private val newReceiptViewModel: NewReceiptViewModel by viewModels({ requireParentFragment() })

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = super.onCreateDialog(savedInstanceState)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)

            return dialog
        }

        private fun setWidthPercent(widthPercentage: Int, heightPercentage: Int) {
            val widthPercent = widthPercentage.toFloat() / 100
            val heightPercent = heightPercentage.toFloat() / 100
            val dm = Resources.getSystem().displayMetrics
            val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
            val percentWidth = rect.width() * widthPercent
            val percentHeight = rect.height() * heightPercent
            dialog?.window?.setLayout(percentWidth.toInt(), percentHeight.toInt())
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            confirmBinding = FinalConfirmationNewReceiptLayoutBinding.inflate(inflater)

            confirmBinding.receiptList.also {
                it.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                val receipts = newReceiptViewModel.getReceipts().toMutableList()
                adapter = ReceiptsAdapter(receipts)
                it.adapter = adapter

                confirmBinding.receiptCount.text = receipts.size.toString()
                confirmBinding.totalMoney.text = newReceiptViewModel.calcTotalPrice()
            }

            confirmBinding.closeBtn.setOnClickListener {
                dismissNow()
            }
            return confirmBinding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setWidthPercent(90, 75)

            confirmBinding.saveBtn.setOnClickListener {
                dismiss()
            }
        }

        inner class ReceiptsAdapter(private val receiptList: MutableList<ReceiptDTO>) : RecyclerView.Adapter<ReceiptsAdapter.ViewHolder>() {

            inner class ViewHolder(private val viewBinding: SummaryReceiptViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {

                fun bind() {
                    val position = adapterPosition
                    val receiptDTO = receiptList[position]
                    viewBinding.productsList.removeAllViews()
                    viewBinding.receiptName.text = receiptDTO.title

                    viewBinding.totalMoney.text = receiptDTO.totalMoney.toString()
                    var productItemBinding: ReceiptProductViewBinding? = null

                    for (product in receiptDTO.getProducts()) {
                        productItemBinding = ReceiptProductViewBinding.inflate(layoutInflater)
                        productItemBinding.productName.text = product.itemName
                        productItemBinding.productPrice.text = product.price.toString()

                        viewBinding.productsList.addView(productItemBinding.root)
                    }
                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(SummaryReceiptViewBinding.inflate(layoutInflater, parent, false))
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.bind()
            }

            override fun getItemCount(): Int = receiptList.size
        }
    }

    interface ReceiptDataGetter {
        fun getTotalMoney(): String
    }
}