package com.example.wapapp2.view.calculation.receipt

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DataTypeConverter
import com.example.wapapp2.commons.classes.DialogSize
import com.example.wapapp2.commons.classes.LoadingDialogView
import com.example.wapapp2.databinding.*
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.example.wapapp2.viewmodel.NewReceiptViewModel


class NewReceiptFragment : Fragment() {
    private var _binding: FragmentNewReceiptBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentCalcRoomID: String

    companion object {
        const val TAG = "NewReceiptFragment"
    }

    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()
    private val newReceiptViewModel: NewReceiptViewModel by viewModels()
    private lateinit var adapter: NewReceiptViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = NewReceiptViewPagerAdapter(this)

        arguments?.apply {
            currentCalcRoomID = getString("currentRoomId")!!
            newReceiptViewModel.calcRoomId = currentCalcRoomID
        }

        newReceiptViewModel.myName = myAccountViewModel.myProfileData.value!!.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNewReceiptBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.viewPager.also {
            it.adapter = adapter
            val indicator = binding.indicator
            it.adapter!!.registerAdapterDataObserver(indicator.adapterDataObserver)
            indicator.setViewPager(it)
        }

        binding.saveBtn.setOnClickListener {
            val confirmReceiptsDialogFragment = ConfirmReceiptsDialogFragment()
            confirmReceiptsDialogFragment.show(childFragmentManager, ConfirmReceiptsDialogFragment.TAG)
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

        newReceiptViewModel.addReceiptResult.observe(viewLifecycleOwner) { //추가 종료
            //영수증 추가 FCM전송
            for (receipt in newReceiptViewModel.getReceipts()) {
                newReceiptViewModel.sendNewReceiptFcm(receipt, newReceiptViewModel.calcRoomId!!)
            }

            LoadingDialogView.clearDialogs()
            Toast.makeText(context, R.string.success_add_receipts, Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter.addFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ConfirmReceiptsDialogFragment : DialogFragment() {
        private var _confirmBinding: FinalConfirmationNewReceiptLayoutBinding? = null
        private val confirmBinding get() = _confirmBinding!!
        private var adapter: ReceiptsAdapter? = null
        private val newReceiptViewModel: NewReceiptViewModel by viewModels({ requireParentFragment() })

        companion object {
            const val TAG = "ConfirmReceiptsDialogFragment"
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = super.onCreateDialog(savedInstanceState)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)

            return dialog
        }


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            _confirmBinding = FinalConfirmationNewReceiptLayoutBinding.inflate(inflater, container, false)

            confirmBinding.receiptList.also {
                it.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                val receipts = newReceiptViewModel.getReceipts().toMutableList()
                adapter = ReceiptsAdapter(receipts)
                it.adapter = adapter

                confirmBinding.receiptCount.text = receipts.size.toString()
                confirmBinding.totalMoney.text = newReceiptViewModel.calcTotalPrice()
            }

            confirmBinding.closeBtn.setOnClickListener {
                dismiss()
            }
            return confirmBinding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            DialogSize.setDialogSize(dialog!!, 95, 80)

            confirmBinding.saveBtn.setOnClickListener {
                //파이어베이스 서버에 등록하는 로직
                newReceiptViewModel.addAllReceipts()
                LoadingDialogView.showDialog(requireActivity(), getString(R.string.adding_receipts))
                dismiss()
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _confirmBinding = null
        }

        private class ReceiptsAdapter(private val receiptList: MutableList<ReceiptDTO>) :
                RecyclerView.Adapter<ReceiptsAdapter.ViewHolder>() {

            private class ViewHolder(private val viewBinding: SummaryReceiptViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {

                fun bind(receiptDTO: ReceiptDTO) {
                    viewBinding.productsList.removeAllViews()
                    viewBinding.receiptName.text = receiptDTO.name
                    val layoutInflater = LayoutInflater.from(viewBinding.root.context)
                    viewBinding.totalMoney.text = DataTypeConverter.toKRW(receiptDTO.totalMoney)
                    var productItemBinding: ReceiptProductInfoViewBinding? = null

                    for (product in receiptDTO.getProducts()) {
                        productItemBinding = ReceiptProductInfoViewBinding.inflate(layoutInflater, viewBinding.productsList, false)
                        productItemBinding.participantGroup.visibility = View.GONE

                        productItemBinding.name.text = product.name
                        productItemBinding.count.text = product.count.toString()
                        productItemBinding.totalPrice.text = DataTypeConverter.toKRW(product.price)

                        viewBinding.productsList.addView(productItemBinding.root)
                    }
                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(SummaryReceiptViewBinding.inflate(LayoutInflater
                    .from(parent.context), parent, false))


            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.bind(receiptList[position])
            }

            override fun getItemCount(): Int = receiptList.size
        }
    }

    interface ReceiptDataGetter {
        fun getTotalMoney(): String
    }
}