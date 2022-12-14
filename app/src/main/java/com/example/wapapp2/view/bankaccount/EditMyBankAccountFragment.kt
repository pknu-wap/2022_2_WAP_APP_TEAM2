package com.example.wapapp2.view.bankaccount

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FinalConfirmationMyBankAccountLayoutBinding
import com.example.wapapp2.databinding.FragmentEditMyBankAccountBinding
import com.example.wapapp2.main.MyApplication
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.BankDTO
import com.example.wapapp2.view.bankaccount.adapter.BankListAdapter
import com.example.wapapp2.viewmodel.MyBankAccountsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditMyBankAccountFragment : Fragment() {
    private val viewModel: MyBankAccountsViewModel by viewModels({ requireActivity() })
    private lateinit var binding: FragmentEditMyBankAccountBinding
    private lateinit var adapter: BankListAdapter
    private lateinit var originalBankAccountDTO: BankAccountDTO
    private val bankOnClickedListener = ListOnClickListener<BankDTO> { item, position ->
        // bankDTO, position을 받음
        viewModel.currentSelectedBank = item
    }

    companion object {
        val TAG = "EditMyBankAccountFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments ?: savedInstanceState

        originalBankAccountDTO = bundle!!.getParcelable("bankAccountDTO")!!
        adapter =
                BankListAdapter(MyApplication.BANK_MAPS.values.toMutableList(), bankOnClickedListener, originalBankAccountDTO.bankDTO!!.uid)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentEditMyBankAccountBinding.inflate(inflater)
        binding.editAccountLayout.accountHolderInputEdit.text = viewModel.defaultBankAccountHolder!!.toEditable()
        binding.editAccountLayout.bankList.adapter = adapter

        binding.editAccountLayout.accountHolderInputEdit.text = originalBankAccountDTO.accountHolder.toEditable()
        binding.editAccountLayout.accountNumberInputEdit.text = originalBankAccountDTO.accountNumber.toEditable()

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editBtn.setOnClickListener {
            // 은행 선택 여부, 계좌번호 입력 여부, 예금주 입력 여부 확인 후 진행
            if (viewModel.currentSelectedBank != null && !binding.editAccountLayout.accountNumberInputEdit.text.isNullOrEmpty()
                    && !binding.editAccountLayout.accountHolderInputEdit.text.isNullOrEmpty()) {
                val modifiedBankAccountDTO =
                        BankAccountDTO(originalBankAccountDTO.id, viewModel.currentSelectedBank!!, binding.editAccountLayout
                                .accountNumberInputEdit.text!!.toString(),
                                binding.editAccountLayout.accountHolderInputEdit.text!!.toString(), viewModel.currentSelectedBank!!.uid)

                //다이얼로그 띄워서 최종 확인 진행
                val dialogViewBinding = FinalConfirmationMyBankAccountLayoutBinding.inflate(layoutInflater)
                dialogViewBinding.bankAccountHolder.text = modifiedBankAccountDTO.accountHolder
                dialogViewBinding.bankAccountNumber.text = modifiedBankAccountDTO.accountNumber
                dialogViewBinding.selectedBank.text = modifiedBankAccountDTO.bankDTO!!.bankName
                dialogViewBinding.icon.setImageResource(modifiedBankAccountDTO.bankDTO!!.iconId)

                MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.final_confirmation)
                        .setView(dialogViewBinding.root).setNegativeButton(R.string.close) { dialog, index ->
                            dialog.dismiss()
                        }.setPositiveButton(R.string.modify) { dialog, index ->
                            viewModel.modifyMyBankAccount(modifiedBankAccountDTO, originalBankAccountDTO)
                            Toast.makeText(context, R.string.edied_my_bank_account, Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }.create().show()
            } else {
                Toast.makeText(context, R.string.please_recheck_the_input_values, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


}