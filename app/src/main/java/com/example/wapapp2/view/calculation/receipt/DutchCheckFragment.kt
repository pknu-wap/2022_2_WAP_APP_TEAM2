package com.example.wapapp2.view.calculation.receipt

import com.example.wapapp2.view.calculation.receipt.adapters.OngoingReceiptsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.collection.arrayMapOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.DutchCheckFragmentBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.CalculationViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel

class DutchCheckFragment() : Fragment() {
    private var _binding: DutchCheckFragmentBinding? = null
    private val binding get() = _binding!!

    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment() })
    private val calculationViewModel by viewModels<CalculationViewModel>({ requireParentFragment() })

    private lateinit var receiptsAdapter: OngoingReceiptsAdapter

    companion object {
        const val TAG = "DutchCheckFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OngoingReceiptsAdapter.MY_UID = calculationViewModel.myUid
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DutchCheckFragmentBinding.inflate(inflater, container, false)

        binding.btnDone.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (calculationViewModel.endCalculation()) {
                    parentFragmentManager.beginTransaction()
                            .replace(R.id.calculation_fragment_container_view, DutchPriceFragment(), DutchPriceFragment.TAG)
                            .addToBackStack(DutchPriceFragment.TAG)
                            .commitAllowingStateLoss()
                } else {
                    Toast.makeText(requireContext().applicationContext, R.string.participants_who_have_not_been_verified, Toast
                            .LENGTH_SHORT).show()
                    binding.btnDone.isChecked = false
                }
            } else {
                val fragmentManager = parentFragmentManager
                if (fragmentManager.findFragmentByTag(DutchPriceFragment.TAG) != null)
                    fragmentManager.popBackStack()
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
        currentCalcRoomViewModel.participantMap.observe(viewLifecycleOwner) {
            currentCalcRoomViewModel.participantMap.removeObservers(viewLifecycleOwner)
            OngoingReceiptsAdapter.PARTICIPANT_COUNT = it.size
            calculationViewModel.loadOngoingReceiptIds()
        }
        receiptsAdapter = OngoingReceiptsAdapter(calculationViewModel)
        binding.viewReceipts.adapter = receiptsAdapter

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