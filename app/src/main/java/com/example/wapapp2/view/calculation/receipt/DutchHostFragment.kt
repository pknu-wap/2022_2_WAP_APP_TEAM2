package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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



        calculationViewModel.onLoadedDataStatus.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(status: Boolean?) {
                if (status!!) {
                    calculationViewModel.onLoadedDataStatus.removeObserver(this)
                    calculationViewModel.onCompletedCalculation.observe(viewLifecycleOwner) { isCompleted ->
                        if (isCompleted) {
                            childFragmentManager.beginTransaction()
                                    .replace(binding.fragmentContainerView.id, FinalDutchFragment(), FinalDutchFragment.TAG)
                                    .commit()
                        } else {
                            childFragmentManager.beginTransaction()
                                    .replace(binding.fragmentContainerView.id, OngoingReceiptsFragment(), OngoingReceiptsFragment.TAG)
                                    .commit()
                        }
                    }
                }

            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}