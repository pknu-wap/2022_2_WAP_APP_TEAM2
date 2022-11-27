package com.example.wapapp2.view.myprofile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.*
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.view.bankaccount.AddMyBankAccountFragment
import com.example.wapapp2.view.bankaccount.EditMyBankAccountFragment
import com.example.wapapp2.view.login.LoginFragment
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.example.wapapp2.viewmodel.MyBankAccountsViewModel
import com.example.wapapp2.viewmodel.MyCalcRoomViewModel
import com.example.wapapp2.viewmodel.MyCalendarViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth


class MyprofileFragment : Fragment() {
    private val myBankAccountsViewModel by activityViewModels<MyBankAccountsViewModel>()
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()
    private val myCalendarViewModel by activityViewModels<MyCalendarViewModel>()
    private val myCalcRoomViewModel by activityViewModels<MyCalcRoomViewModel>()
    private var _binding: FragmentMyprofileBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyBankAccountsAdapter

    companion object {
        const val TAG = "MyProfile"
    }

    private val onClickedPopupMenuListener = object : OnClickedPopupMenuListener {
        override fun onClickedRemove(bankAccountDTO: BankAccountDTO, position: Int) {
            val dialogViewBinding = FinalConfirmationMyBankAccountLayoutBinding.inflate(layoutInflater)
            dialogViewBinding.bankAccountHolder.text = bankAccountDTO.accountHolder
            dialogViewBinding.bankAccountNumber.text = bankAccountDTO.accountNumber
            dialogViewBinding.selectedBank.text = bankAccountDTO.bankDTO!!.bankName
            dialogViewBinding.icon.setImageResource(bankAccountDTO.bankDTO!!.iconId)

            MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.remove_my_account)
                    .setView(dialogViewBinding.root).setNegativeButton(R.string.close) { dialog, index ->
                        dialog.dismiss()
                    }.setPositiveButton(R.string.remove) { dialog, index ->
                        myBankAccountsViewModel.removeMyBankAccount(bankAccountDTO)
                        Toast.makeText(context, R.string.removed_bank_account, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }.create().show()
        }

        override fun onClickedEdit(bankAccountDTO: BankAccountDTO, position: Int) {
            val fragment = EditMyBankAccountFragment()
            fragment.arguments = Bundle().also {
                it.putParcelable("bankAccountDTO", bankAccountDTO)
            }
            parentFragmentManager
                    .beginTransaction()
                    .hide(this@MyprofileFragment)
                    .add(R.id.fragment_container_view, fragment, EditMyBankAccountFragment.TAG)
                    .addToBackStack(EditMyBankAccountFragment.TAG)
                    .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBankAccountsViewModel.defaultBankAccountHolder = myAccountViewModel.myProfileData.value!!.name
        adapter = MyBankAccountsAdapter(myBankAccountsViewModel.getMyBankAccountsOptions(), onClickedPopupMenuListener)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMyprofileBinding.inflate(inflater, container, false)

        binding.loadingView.setContentView(binding.bankList)
        binding.loadingView.onSuccessful()

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.bankList.adapter = adapter

        binding.btnEdit.setOnClickListener {
            val dialog = DialogEditDetailFragment()
            dialog.show(parentFragmentManager, DialogEditDetailFragment.TAG)
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val loginFragment = LoginFragment()

            myCalendarViewModel.listenerRemove()
            myCalcRoomViewModel.listenerRemove()

            parentFragmentManager
                    .beginTransaction()
                    .hide(this@MyprofileFragment)
                    .add(R.id.fragment_container_view, loginFragment, LoginFragment::class.java.name)
                    .commitAllowingStateLoss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddAcount.setOnClickListener {
            val addFragment = AddMyBankAccountFragment()
            parentFragmentManager.beginTransaction().hide(this@MyprofileFragment)
                    .add(R.id.fragment_container_view, addFragment, AddMyBankAccountFragment.TAG)
                    .addToBackStack(AddMyBankAccountFragment.TAG).commit()
        }

        myAccountViewModel.myProfileData.observe(viewLifecycleOwner) {
            binding.myProfileName.text = it.name
            binding.myAccountId.text = it.email
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            adapter.stopListening()
        } else {
            adapter.startListening()
        }
    }

    interface OnClickedPopupMenuListener {
        fun onClickedRemove(bankAccountDTO: BankAccountDTO, position: Int)
        fun onClickedEdit(bankAccountDTO: BankAccountDTO, position: Int)
    }
}