package com.example.wapapp2.view.calculation.receipt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.OngoingDutchFragmentBinding
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.calculation.receipt.adapters.OngoingReceiptsAdapter
import com.example.wapapp2.viewmodel.CalculationViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel

class OngoingReceiptsFragment() : Fragment() {
    private var _binding: OngoingDutchFragmentBinding? = null
    private val binding get() = _binding!!

    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment().requireParentFragment() })
    private val calculationViewModel by viewModels<CalculationViewModel>({ requireParentFragment().requireParentFragment() })

    private lateinit var receiptsAdapter: OngoingReceiptsAdapter

    companion object {
        const val TAG = "OngoingReceiptsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OngoingReceiptsAdapter.MY_UID = calculationViewModel.myUid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = OngoingDutchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCalcAdd.setOnClickListener {
            //영수증 추가
            val fragment = NewReceiptFragment()
            fragment.arguments = Bundle().apply {
                putString("currentRoomId", currentCalcRoomViewModel.roomId)
            }

            val fragmentManager = requireParentFragment().requireParentFragment().parentFragmentManager
            fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(CalcMainFragment.TAG)!!)
                    .add(R.id.fragment_container_view, fragment, NewReceiptFragment.TAG)
                    .addToBackStack(NewReceiptFragment.TAG).commit()
        }

        binding.calcBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                receiptsAdapter.lockCheckBox()
                calculationViewModel.confirmMyCalculation()
            } else {
                receiptsAdapter.unlockCheckBox()
                calculationViewModel.requestModifyCalculation()
            }
        }

        receiptsAdapter = OngoingReceiptsAdapter(calculationViewModel)
        binding.receiptList.adapter = receiptsAdapter

        calculationViewModel.receiptMap.observe(viewLifecycleOwner) { result ->
            receiptsAdapter.receiptMap.clear()
            receiptsAdapter.receiptMap.putAll(result.toMap())
            receiptsAdapter.notifyDataSetChanged()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}