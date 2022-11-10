package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.BankItemViewBinding
import com.example.wapapp2.databinding.DutchCheckFragmentBinding
import com.example.wapapp2.databinding.ViewDutchItemBinding
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.FixedPayDTO
import com.example.wapapp2.view.bankaccount.BankTransferDialogFragment
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.calculation.interfaces.OnUpdateMoneyCallback
import java.text.DecimalFormat

class DutchPriceFragment(onUpdateMoneyCallback: OnUpdateMoneyCallback) : Fragment() {
    lateinit var binding : DutchCheckFragmentBinding

    private var onUpdateMoneyCallback: OnUpdateMoneyCallback

    init{
        this.onUpdateMoneyCallback = onUpdateMoneyCallback
    }


    private val onClickedBankAccountListener =
        ListOnClickListener<BankAccountDTO> { bankAccountDTO, position ->
            onClickedBankAccount(bankAccountDTO)
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DutchCheckFragmentBinding.inflate(inflater)
        binding.btnAdd.setOnClickListener(View.OnClickListener {
            TODO(" 영수증 추가 화면 연결 ")
        })

        binding.btnDone.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                TODO(" 서버에 정보 전송 필요 -> 모든 인원 확정 시 확정화면 ")
            }
            else{
                // 임시적으로 모든 인원이 check된 상황으로 구현

            }
        })
        binding.viewReceipts.adapter =
            FixedPayAdapter(DummyData.getFixedDTOs(), onClickedBankAccountListener, onUpdateMoneyCallback)

        return binding.root
    }

    /** 확정 정산 금액 **/
    private class FixedPayAdapter(val items: ArrayList<FixedPayDTO>, val onClickedBankAccountListener:
    ListOnClickListener<BankAccountDTO>, val onUpdateMoneyCallback: OnUpdateMoneyCallback
    )
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        private class FixedPayVH(val binding: ViewDutchItemBinding, val onClickedBankAccountListener: ListOnClickListener<BankAccountDTO>,
                                 val onUpdateMoneyCallback: OnUpdateMoneyCallback
        ) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(item: FixedPayDTO) {
                binding.name.text = item.name
                binding.pay.text = DecimalFormat("#,###").format(item.pay)
                if (item.pay >= 0) {
                    binding.pay.text = "+" + binding.pay.text
                    binding.pay.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.payPlus
                        )
                    )
                    binding.accounts.adapter = AccountsAdapter(null, onClickedBankAccountListener)

                } else {
                    binding.pay.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.payMinus
                        )
                    )
                    binding.accounts.adapter = AccountsAdapter(item.accounts, onClickedBankAccountListener)

                }
                onUpdateMoneyCallback.onUpdateMoney(item.pay)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return FixedPayVH(
                ViewDutchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickedBankAccountListener, onUpdateMoneyCallback)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as FixedPayVH).bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }


        private class AccountsAdapter(val items: ArrayList<BankAccountDTO>?,
                                      val onClickedBankAccountListener: ListOnClickListener<BankAccountDTO>
        )
            : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            private class AccountsVH(val binding: BankItemViewBinding,
                                     val onClickedBankAccountListener: ListOnClickListener<BankAccountDTO>
            ) :
                RecyclerView.ViewHolder(binding.root) {
                fun bind_account(account: BankAccountDTO) {
                    binding.name.textSize = 14.0F
                    binding.name.text = "${account.bankDTO.bankName}  ${account.accountNumber}  ${account.accountHolder}"

                    binding.root.setOnClickListener {
                        onClickedBankAccountListener.onClicked(account, adapterPosition)
                    }
                }

                fun bindAlert() {
                    binding.name.textSize = 14.0f
                    binding.name.text = "정산 재촉하기"
                    binding.icon.visibility = View.INVISIBLE
                    binding.root.setOnClickListener {
                        Toast.makeText(binding.root.context, "정산 재촉하기 구현 필요", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return AccountsVH(BankItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickedBankAccountListener)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (items != null) (holder as AccountsVH).bind_account(items[position])
                else (holder as AccountsVH).bindAlert()
            }

            override fun getItemCount(): Int {
                return items?.size ?: 1
            }

        }


    }




    /** show Bank App dialog **/
    private fun onClickedBankAccount(account: BankAccountDTO) {
        val fragment = BankTransferDialogFragment.newInstance(account)
        fragment.show(childFragmentManager, "bankTransferDialog")
    }




}