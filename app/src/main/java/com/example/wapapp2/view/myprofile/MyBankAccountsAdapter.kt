package com.example.wapapp2.view.myprofile

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.databinding.BankAccountListItemBinding
import com.example.wapapp2.model.BankAccountDTO
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

class MyBankAccountsAdapter(
        options: FirestoreRecyclerOptions<BankAccountDTO>,
        private val onClickedPopupMenuListener: MyprofileFragment.OnClickedPopupMenuListener,
) :
        FirestoreRecyclerAdapter<BankAccountDTO,
                MyBankAccountsAdapter.ViewHolder>(options), IAdapterItemCount {

    class ViewHolder(
            private val viewBinding: BankAccountListItemBinding,
            private val onClickedPopupMenuListener: MyprofileFragment.OnClickedPopupMenuListener,
    ) :
            RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(bankAccountDTO: BankAccountDTO) {
            val position = bindingAdapterPosition

            viewBinding.bankAccountHolder.text = bankAccountDTO.accountHolder
            viewBinding.bankAccountNumber.text = bankAccountDTO.accountNumber
            viewBinding.bankName.text = bankAccountDTO.bankDTO!!.bankName
            viewBinding.icon.setImageResource(bankAccountDTO.bankDTO!!.iconId)

            viewBinding.moreBtn.setOnClickListener { moreBtnView ->
                //PopupMenu 객체 생성
                val popup = PopupMenu(viewBinding.root.context, moreBtnView, Gravity.BOTTOM);
                popup.menuInflater.inflate(R.menu.my_bank_account_click_popup, popup.menu);
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_remove -> {
                            onClickedPopupMenuListener.onClickedRemove(bankAccountDTO, position)
                        }
                        R.id.menu_edit -> {
                            onClickedPopupMenuListener.onClickedEdit(bankAccountDTO, position)
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
        return ViewHolder(BankAccountListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickedPopupMenuListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BankAccountDTO) {
        holder.bind(model)
    }

    override fun onDataChanged() {
        super.onDataChanged()
    }

    override fun getAdapterItemCount() = itemCount

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
        super.onChildChanged(type, snapshot, newIndex, oldIndex)
    }

}