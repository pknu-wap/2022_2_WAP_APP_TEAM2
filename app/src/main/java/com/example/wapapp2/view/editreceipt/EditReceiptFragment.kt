package com.example.wapapp2.view.editreceipt

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentEditReceiptBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.checkreceipt.CheckReceiptAdapter
import com.example.wapapp2.view.checkreceipt.ReceiptList
import com.example.wapapp2.viewmodel.ReceiptViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditReceiptFragment : Fragment() {

    private var _binding: FragmentEditReceiptBinding? = null
    private val binding get() = _binding!!
    private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd E a hh:mm", Locale.getDefault())

    private var calcRoomId: String? = null
    private var receiptDTO: ReceiptDTO? = null

    private val receiptViewModel by viewModels<ReceiptViewModel>()
    private val onUpdatedValueListener = OnUpdatedValueListener {
        val newTotalMoney = receiptViewModel.calcTotalPrice()
        receiptDTO?.totalMoney = newTotalMoney.toInt()
        binding.totalMoney.text = receiptDTO?.totalMoney.toString()
    }
    private val adapter = EditReceiptAdapter(onUpdatedValueListener = onUpdatedValueListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            calcRoomId = getString("calcRoomId")
            receiptDTO = getParcelable("receiptDTO")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditReceiptBinding.inflate(inflater)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        binding.rvEditreceipt.setHasFixedSize(true)
        binding.rvEditreceipt.adapter = adapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiptDTO?.apply {
            binding.totalMoney.text = totalMoney.toString().toEditable()
            binding.titleText.text = name.toEditable()
            binding.dateTime.text = simpleDateFormat.format(createdTime!!)
        }

        receiptViewModel.products.observe(viewLifecycleOwner) {
            adapter.items = it
        }
        receiptViewModel.getProducts(receiptDTO?.id!!, calcRoomId!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun interface OnUpdatedValueListener {
        fun onUpdated()
    }

    private fun String.toEditable(): Editable = Editable.Factory().newEditable(this)

}