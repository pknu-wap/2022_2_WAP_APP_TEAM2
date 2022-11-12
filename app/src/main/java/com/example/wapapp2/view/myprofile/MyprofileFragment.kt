package com.example.wapapp2.view.myprofile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.databinding.*
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.view.bankaccount.AddMyBankAccountFragment
import com.example.wapapp2.view.bankaccount.EditMyBankAccountFragment
import com.example.wapapp2.view.login.LoginFragment
import com.example.wapapp2.viewmodel.MyBankAccountsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth


class MyprofileFragment : Fragment() {
    private val viewModel: MyBankAccountsViewModel by viewModels()
    private lateinit var binding: FragmentMyprofileBinding
    private val adapter = MyAccountListAdapter()

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
                        viewModel.removeMyBankAccount(bankAccountDTO.id)
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
                    .add(R.id.fragment_container_view, fragment, "MyprofileFragment")
                    .addToBackStack("MyprofileFragment")
                    .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.defaultBankAccountHolder = "박준성"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMyprofileBinding.inflate(inflater)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.bankList.adapter = adapter

        binding.btnEdit.setOnClickListener {
            val dialog = DialogEditDetailFragment()
            dialog
                    .show(parentFragmentManager, "CustomDialog")
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
                    .add(R.id.fragment_container_view, addFragment, "AddMyBankAccountFragment")
                    .addToBackStack("AddMyBankAccountFragment").commit()
        }
    }

    inner class MyAccountListAdapter : RecyclerView.Adapter<MyAccountListAdapter.ViewHolder>() {
        private val list = ArrayList<BankAccountDTO>()

        fun setList(list: ArrayList<BankAccountDTO>) {
            this.list.addAll(list)
        }

        inner class ViewHolder(private val viewBinding: BankAccountListItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

            fun bind() {
                val position = bindingAdapterPosition

                viewBinding.bankAccountHolder.text = list[position].accountHolder
                viewBinding.bankAccountNumber.text = list[position].accountNumber
                viewBinding.bankName.text = list[position].bankDTO!!.bankName
                viewBinding.icon.setImageResource(list[position].bankDTO!!.iconId)

                viewBinding.moreBtn.setOnClickListener { moreBtnView ->
                    //PopupMenu 객체 생성
                    val popup = PopupMenu(requireContext(), moreBtnView, Gravity.BOTTOM);
                    popup.menuInflater.inflate(R.menu.my_bank_account_click_popup, popup.menu);
                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.menu_remove -> {
                                onClickedPopupMenuListener.onClickedRemove(list[position], position)
                            }
                            R.id.menu_edit -> {
                                onClickedPopupMenuListener.onClickedEdit(list[position], position)
                            }
                            else -> {}
                        }
                        true
                    }

                    popup.show();
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(BankAccountListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind()
        }

        override fun getItemCount(): Int = list.size
    }

    interface OnClickedPopupMenuListener {
        fun onClickedRemove(bankAccountDTO: BankAccountDTO, position: Int)
        fun onClickedEdit(bankAccountDTO: BankAccountDTO, position: Int)
    }
}