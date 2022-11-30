package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ItemOnClickListener
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FinalDutchFragmentBinding
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.FinalTransferDTO
import com.example.wapapp2.view.bankaccount.BankTransferDialogFragment
import com.example.wapapp2.view.calculation.receipt.adapters.FinalDutchAdapter
import com.example.wapapp2.viewmodel.CalculationViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.example.wapapp2.viewmodel.RushCalcViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FinalDutchFragment : Fragment() {
    private var _binding: FinalDutchFragmentBinding? = null
    private val binding get() = _binding!!

    private val calculationViewModel by viewModels<CalculationViewModel>({ requireParentFragment().requireParentFragment() })
    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment().requireParentFragment() })
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()

    private val rushCalcRoomViewModel by viewModels<RushCalcViewModel>()

    private val accountOnClickListener = ListOnClickListener<BankAccountDTO> { item, pos ->
        val fragment = BankTransferDialogFragment.newInstance(item)
        fragment.show(childFragmentManager, BankTransferDialogFragment.TAG)
    }

    private val requestCalcOnClickListener = ItemOnClickListener<FinalTransferDTO> { item ->
        val myUid = calculationViewModel.myUid

        rushCalcRoomViewModel.selectedRecipientTokens.clear()
        for (participant in rushCalcRoomViewModel.calcRoomParticipants.values) {
            if (participant.userId != myUid)
                rushCalcRoomViewModel.selectedRecipientTokens.add(participant.fcmToken)
        }

        MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.calc_rush)
                .setMessage(R.string.msg_request_rush_calc)
                .setNegativeButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }.setPositiveButton(R.string.check) { dialog, which ->
                    rushCalcRoomViewModel.sendRushCalcFcm()
                    dialog.dismiss()
                }.create().show()
    }


    private val finalDutchAdapter = FinalDutchAdapter(accountOnClickListener, requestCalcOnClickListener)

    companion object {
        const val TAG = "FinalDutchFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rushCalcRoomViewModel.myUserName = myAccountViewModel.myProfileData.value!!.name
        rushCalcRoomViewModel.myUid = myAccountViewModel.myProfileData.value!!.id
        rushCalcRoomViewModel.calcRoomId = currentCalcRoomViewModel.roomId!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FinalDutchFragmentBinding.inflate(inflater, container, false)

        binding.finalReceipts.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.finalReceipts.adapter = finalDutchAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calculationViewModel.finalTransferData.observe(viewLifecycleOwner) {
            finalDutchAdapter.items.clear()
            finalDutchAdapter.items.addAll(it)
            finalDutchAdapter.notifyDataSetChanged()
        }

        currentCalcRoomViewModel.participantMap.value!!.apply {
            rushCalcRoomViewModel.calcRoomParticipants.clear()
            rushCalcRoomViewModel.calcRoomParticipants.putAll(this)
        }

        calculationViewModel.loadFinalTransferData()

        binding.modifyCalc.setOnClickListener {
            calculationViewModel.requestModifyCalculation()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}