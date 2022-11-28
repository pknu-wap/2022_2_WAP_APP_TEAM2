package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.DutchCheckFragmentBinding
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.view.bankaccount.BankTransferDialogFragment
import com.example.wapapp2.viewmodel.CalculationViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel

class DutchPriceFragment : Fragment() {
    private var _binding: DutchCheckFragmentBinding? = null
    private val binding get() = _binding!!

    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment() })
    private val calculationViewModel by viewModels<CalculationViewModel>({ requireParentFragment() })

    companion object {
        const val TAG = "DutchPriceFragment"
    }


    private val onClickedBankAccountListener =
            ListOnClickListener<BankAccountDTO> { bankAccountDTO, position ->
                onClickedBankAccount(bankAccountDTO)
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DutchCheckFragmentBinding.inflate(inflater, container, false)
        binding.btnAdd.setOnClickListener(View.OnClickListener {
            TODO(" 영수증 추가 화면 연결 ")
        })

        binding.btnDone.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                TODO(" 서버에 정보 전송 필요 -> 모든 인원 확정 시 확정화면 ")
            } else {
                // 임시적으로 모든 인원이 check된 상황으로 구현

            }
        })
        /*
        binding.viewReceipts.adapter =
                FixedPriceAdapter(DummyData.getFixedDTOs(), onClickedBankAccountListener, onUpdateMoneyCallback)


         */
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /** show Bank App dialog **/
    private fun onClickedBankAccount(account: BankAccountDTO) {
        val fragment = BankTransferDialogFragment.newInstance(account)
        fragment.show(childFragmentManager, BankTransferDialogFragment.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}