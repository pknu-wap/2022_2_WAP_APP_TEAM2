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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentCalcRoomViewModel.participantMap.observe(viewLifecycleOwner) {
            OngoingReceiptsAdapter.PARTICIPANT_COUNT = it.size
        }

        receiptsAdapter = OngoingReceiptsAdapter(calculationViewModel)
        binding.finalReceipts.adapter = receiptsAdapter

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