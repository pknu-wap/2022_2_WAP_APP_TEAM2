package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentDutchHostBinding
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.CalculationViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel


class DutchHostFragment : Fragment() {
    private var _binding: FragmentDutchHostBinding? = null
    private val binding get() = _binding!!

    private val calculationViewModel by viewModels<CalculationViewModel>({ requireParentFragment() })
    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment() })

    companion object {
        const val TAG = "DutchHostFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDutchHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDone.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (calculationViewModel.endCalculation()) {
                    childFragmentManager.beginTransaction().remove(this).commit()
                    childFragmentManager.beginTransaction().add(R.id.calculation_fragment_container_view, DutchPriceFragment(), DutchPriceFragment.TAG)
                            .addToBackStack(DutchPriceFragment.TAG).commit()
                } else {
                    Toast.makeText(requireContext().applicationContext, R.string.participants_who_have_not_been_verified, Toast
                            .LENGTH_SHORT).show()
                    binding.btnDone.isChecked = false
                }
            } else {
                val fragmentManager = childFragmentManager
                if (fragmentManager.findFragmentByTag(DutchPriceFragment.TAG) != null)
                    fragmentManager.popBackStack()
            }
        }

        binding.btnCalcAdd.setOnClickListener {
            //영수증 추가
            val fragment = NewReceiptFragment()
            fragment.arguments = Bundle().apply {
                putString("currentRoomId", currentCalcRoomViewModel.roomId)
            }

            val fragmentManager = requireParentFragment().requireParentFragment().parentFragmentManager
            fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(CalcMainFragment.TAG) as Fragment)
                    .add(R.id.fragment_container_view, fragment, NewReceiptFragment.TAG)
                    .addToBackStack(NewReceiptFragment.TAG).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}