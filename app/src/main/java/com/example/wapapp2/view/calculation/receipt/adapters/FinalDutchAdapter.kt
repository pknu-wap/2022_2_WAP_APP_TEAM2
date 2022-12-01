package com.example.wapapp2.view.calculation.receipt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DataTypeConverter
import com.example.wapapp2.commons.interfaces.ItemOnClickListener
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.BankItemViewBinding
import com.example.wapapp2.databinding.ViewDutchItemBinding
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.FinalTransferDTO

/** 확정 정산 금액 **/
class FinalDutchAdapter(
        private val accountOnClickListener: ListOnClickListener<BankAccountDTO>,
        private val requestCalcOnClickListener: ItemOnClickListener<FinalTransferDTO>,
        private val myUid: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val items = mutableListOf<FinalTransferDTO>()

    class FixedPayVH(
            private val binding: ViewDutchItemBinding, private val accountOnClickListener: ListOnClickListener<BankAccountDTO>,
            private val requestCalcOnClickListener: ItemOnClickListener<FinalTransferDTO>,
            private val myUid: String,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FinalTransferDTO) {
            binding.payersName.text = item.payersName
            binding.money.text = if (item.payersId != myUid) DataTypeConverter.toKRW(item.transferMoney)
            else DataTypeConverter.toKRW(item.totalMoney + item.transferMoney)

            binding.type.text = if (item.payersId != myUid) binding.root.context.getString(R.string.money_to_give)
            else binding.root.context.getString(R.string.money_to_be_received)

            if (item.payersId != myUid) {
                binding.accounts.adapter =
                        AccountsAdapter(item, item.accounts.toList(), accountOnClickListener, requestCalcOnClickListener)
            } else {
                binding.accounts.adapter = AccountsAdapter(item, null, accountOnClickListener,
                        requestCalcOnClickListener)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            FixedPayVH(ViewDutchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), accountOnClickListener,
                    requestCalcOnClickListener, myUid)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FixedPayVH).bind(items[position])
    }


    override fun getItemCount() = items.size

    private class AccountsAdapter(
            private val finalTransferDTO: FinalTransferDTO,
            private val items: List<BankAccountDTO>?,
            private val onClickedBankAccountListener: ListOnClickListener<BankAccountDTO>,
            private val requestCalcOnClickListener: ItemOnClickListener<FinalTransferDTO>,
    ) : RecyclerView.Adapter<AccountsAdapter.AccountsVH>() {

        class AccountsVH(
                private val binding: BankItemViewBinding,
                private val onClickedBankAccountListener: ListOnClickListener<BankAccountDTO>,
                private val requestCalcOnClickListener: ItemOnClickListener<FinalTransferDTO>,
        ) :
                RecyclerView.ViewHolder(binding.root) {
            fun bind(account: BankAccountDTO) {
                binding.name.textSize = 14.0F
                Glide.with(binding.root).load(account.bankDTO!!.iconId).into(binding.icon)
                val valueText = "${account.bankDTO!!.bankName}  ${account.accountNumber}  ${account.accountHolder}"
                binding.name.text = valueText

                binding.root.setOnClickListener {
                    onClickedBankAccountListener.onClicked(account, bindingAdapterPosition)
                }
            }

            fun bindAlert(finalTransferDTO: FinalTransferDTO) {
                binding.name.textSize = 14.0f
                binding.name.text = "정산 재촉하기"
                binding.icon.visibility = View.INVISIBLE
                binding.root.setOnClickListener {
                    requestCalcOnClickListener.onClicked(finalTransferDTO)
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsVH =
                AccountsVH(BankItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickedBankAccountListener,
                        requestCalcOnClickListener)

        override fun onBindViewHolder(holder: AccountsVH, position: Int) {
            if (!items.isNullOrEmpty()) holder.bind(items[position])
            else holder.bindAlert(finalTransferDTO)
        }

        override fun getItemCount() = items?.size ?: 1
    }


}