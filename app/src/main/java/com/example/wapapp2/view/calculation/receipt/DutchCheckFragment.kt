package com.example.wapapp2.view.calculation.receipt

import ReceiptAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.databinding.DutchCheckFragmentBinding
import com.example.wapapp2.databinding.ViewReceiptItemBinding
import com.example.wapapp2.databinding.ViewRecentCalcItemBinding
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.calculation.interfaces.OnFixOngoingCallback
import com.example.wapapp2.view.calculation.interfaces.OnUpdateSummaryCallback
import com.example.wapapp2.view.main.MainHostFragment
import com.example.wapapp2.viewmodel.ReceiptViewModel
import org.joda.time.DateTime
import java.text.DecimalFormat

class DutchCheckFragment(onFixOngoingReceipt: OnFixOngoingCallback, onUpdateSummaryCallback: OnUpdateSummaryCallback) : Fragment() {
    private lateinit var binding: DutchCheckFragmentBinding
    val onFixOngoingReceipt: OnFixOngoingCallback
    val onUpdateSummaryCallback: OnUpdateSummaryCallback
    val receiptViewModel: ReceiptViewModel by viewModels({ requireParentFragment() })

    init {
        this.onFixOngoingReceipt = onFixOngoingReceipt
        this.onUpdateSummaryCallback = onUpdateSummaryCallback
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        binding = DutchCheckFragmentBinding.inflate(inflater)

        binding.viewReceipts.adapter =
                ReceiptAdapter(requireContext(), DummyData.getReceipts(), this.receiptViewModel, this.onUpdateSummaryCallback)
        onUpdateSummaryCallback.updateSummaryUI(receiptViewModel.getCurrentSummary)
        binding.btnDone.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
            } else {
                //임시로 구현 -> 팀 인원 다 체크후 해야함
                onFixOngoingReceipt.onFixOngoingReceipt()
            }
        })
        binding.btnAdd.setOnClickListener {
            //영수증 추가
            val fragment = NewReceiptFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager

            fragmentManager.beginTransaction()
                .hide(fragmentManager.findFragmentByTag(CalcMainFragment.TAG) as Fragment)
                .add(R.id.fragment_container_view, fragment, NewReceiptFragment.TAG)
                .addToBackStack(NewReceiptFragment.TAG).commit()
        }

        return binding.root
    }


}