package com.example.wapapp2.view.calculation.receipt

import ReceiptAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.DutchCheckFragmentBinding
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.CalculationViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel

class DutchCheckFragment() : Fragment() {
    private var _binding: DutchCheckFragmentBinding? = null
    private val binding get() = _binding!!

    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment() })
    private val calculationViewModel by viewModels<CalculationViewModel>({ requireParentFragment() })

    private val receiptsAdapter = ReceiptAdapter(calculationViewModel)

    companion object {
        const val TAG = "DutchCheckFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DutchCheckFragmentBinding.inflate(inflater, container, false)

        binding.viewReceipts.adapter = receiptsAdapter

        binding.btnDone.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
            } else {
                //임시로 구현 -> 팀 인원 다 체크후 해야함
                parentFragmentManager.beginTransaction()
                        .replace(R.id.calculation_fragment_container_view, DutchPriceFragment(), DutchPriceFragment.TAG)
                        .commitAllowingStateLoss()
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}