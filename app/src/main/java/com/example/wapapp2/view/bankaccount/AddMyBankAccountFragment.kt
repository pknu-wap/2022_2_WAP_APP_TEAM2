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
import com.example.wapapp2.commons.classes.LoadingDialogView
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FinalConfirmationMyBankAccountLayoutBinding
import com.example.wapapp2.databinding.FragmentAddMyBankAccountBinding
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.BankDTO
import com.example.wapapp2.view.bankaccount.adapter.BankListAdapter
import com.example.wapapp2.viewmodel.MyBankAccountsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddMyBankAccountFragment : Fragment() {
    private val myBankAccountsViewModel: MyBankAccountsViewModel by viewModels({ requireParentFragment() })
    private var _binding: FragmentAddMyBankAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BankListAdapter
    private val bankOnClickedListener = ListOnClickListener<BankDTO> { item, position ->
        // bankDTO, position을 받음
        myBankAccountsViewModel.selectedBank = item
    }

    companion object {
        val TAG = "AddMyBankAccountFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = BankListAdapter(myBankAccountsViewModel.bankList, bankOnClickedListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAddMyBankAccountBinding.inflate(inflater)

        binding.editAccountLayout.accountHolderInputEdit.text = myBankAccountsViewModel.defaultBankAccountHolder!!.toEditable()
        binding.editAccountLayout.bankList.adapter = adapter

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addBtn.setOnClickListener {
            // 은행 선택 여부, 계좌번호 입력 여부, 예금주 입력 여부 확인 후 진행
            if (myBankAccountsViewModel.selectedBank != null && !binding.editAccountLayout.accountNumberInputEdit.text.isNullOrEmpty()
                    && !binding.editAccountLayout.accountHolderInputEdit.text.isNullOrEmpty()) {
                val selectedBank = myBankAccountsViewModel.selectedBank!!
                val bankAccountDTO = BankAccountDTO("", null, accountNumber = binding.editAccountLayout.accountNumberInputEdit.text!!
                        .toString(), accountHolder = binding.editAccountLayout.accountHolderInputEdit.text!!.toString(), bankId = selectedBank.uid)

                //다이얼로그 띄워서 최종 확인 진행
                val dialogViewBinding = FinalConfirmationMyBankAccountLayoutBinding.inflate(layoutInflater)
                dialogViewBinding.bankAccountHolder.text = bankAccountDTO.accountHolder
                dialogViewBinding.bankAccountNumber.text = bankAccountDTO.accountNumber
                dialogViewBinding.selectedBank.text = selectedBank.bankName
                dialogViewBinding.icon.setImageResource(selectedBank.iconId)

                MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.final_confirmation)
                        .setView(dialogViewBinding.root).setNegativeButton(R.string.cancel) { dialog, index ->
                            dialog.dismiss()
                        }.setPositiveButton(R.string.add) { dialog, index ->
                            LoadingDialogView.showDialog(requireActivity(), getString(R.string.adding_my_bank_account))
                            myBankAccountsViewModel.addMyBankAccount(bankAccountDTO)
                            dialog.dismiss()
                        }.create().show()

            } else {
                Toast.makeText(context, R.string.please_recheck_the_input_values, Toast.LENGTH_SHORT).show()
            }
        }

        myBankAccountsViewModel.addedMyBankAccount.observe(viewLifecycleOwner) {
            Toast.makeText(context, R.string.added_new_bank_account, Toast.LENGTH_SHORT).show()
            LoadingDialogView.clearDialogs()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


}