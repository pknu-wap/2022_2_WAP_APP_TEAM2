package com.example.wapapp2.view.myprofile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.*
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.view.bankaccount.AddMyBankAccountFragment
import com.example.wapapp2.view.bankaccount.EditMyBankAccountFragment
import com.example.wapapp2.view.login.LoginFragment
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.example.wapapp2.viewmodel.MyBankAccountsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth


class MyprofileFragment : Fragment() {
    private val myBankAccountsViewModel: MyBankAccountsViewModel by viewModels({ requireActivity() })
    private val myAccountViewModel by viewModels<MyAccountViewModel>({ requireActivity() })
    private var _binding: FragmentMyprofileBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyBankAccountsAdapter

    companion object {
        val TAG = "MyProfile"
    }

    private val onClickedPopupMenuListener = object : OnClickedPopupMenuListener {
        override fun onClickedRemove(bankAccountDTO: BankAccountDTO, position: Int) {
            val dialogViewBinding = FinalConfirmationMyBankAccountLayoutBinding.inflate(layoutInflater)
            dialogViewBinding.bankAccountHolder.text = bankAccountDTO.accountHolder
            dialogViewBinding.bankAccountNumber.text = bankAccountDTO.accountNumber
            dialogViewBinding.selectedBank.text = bankAccountDTO.bankDTO!!.bankName
            dialogViewBinding.icon.setImageResource(bankAccountDTO.bankDTO!!.iconId)

            val dialog = MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.remove_my_account)
                    .setView(dialogViewBinding.root).setNegativeButton(R.string.exit) { dialog, index ->
                        dialog.dismiss()
                    }.setPositiveButton(R.string.remove) { dialog, index ->
                        myBankAccountsViewModel.removeMyBankAccount(bankAccountDTO)
                        Toast.makeText(context, R.string.removed_bank_account, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }.create()

            dialog.show()
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
        myBankAccountsViewModel.defaultBankAccountHolder = myAccountViewModel.myName
        adapter = MyBankAccountsAdapter(myBankAccountsViewModel.getMyBankAccountsOptions(), onClickedPopupMenuListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentMyprofileBinding.inflate(inflater, container, false)

        binding.loadingView.setContentView(binding.bankList)
        binding.loadingView.onSuccessful()

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.bankList.adapter = adapter
        adapter.startListening()

        binding.btnEdit.setOnClickListener {
            val dialog = DialogEditDetailFragment()
            dialog.show(parentFragmentManager, "CustomDialog")
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val loginFragment = LoginFragment()

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
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    interface OnClickedPopupMenuListener {
        fun onClickedRemove(bankAccountDTO: BankAccountDTO, position: Int)
        fun onClickedEdit(bankAccountDTO: BankAccountDTO, position: Int)
    }
}