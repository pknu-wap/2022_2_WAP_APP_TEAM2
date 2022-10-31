package com.example.wapapp2.view.bankaccount.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.BankItemViewBinding
import com.example.wapapp2.model.BankDTO

class BankListAdapter(private val bankList: ArrayList<BankDTO>,
                      private val bankOnClickedListener: ListOnClickListener<BankDTO>, private var selectedBankId: String? = null) :
        RecyclerView
        .Adapter<BankListAdapter.ViewHolder>() {
    private var selectedPosition = 0
    private val unselectedBackgroundTint = ColorStateList.valueOf(Color.WHITE)
    private val selectedBackgroundTint = ColorStateList.valueOf(Color.LTGRAY)

    inner class ViewHolder(private val viewBinding: BankItemViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind() {
            val position = adapterPosition

            if (selectedBankId != null && selectedBankId == bankList[position].uid) {
                viewBinding.root.backgroundTintList = selectedBackgroundTint
            } else {
                viewBinding.root.backgroundTintList = unselectedBackgroundTint
            }

            viewBinding.icon.setImageResource(bankList[position].iconId)
            viewBinding.name.text = bankList[position].bankName

            viewBinding.root.setOnClickListener {
                selectedBankId?.apply { notifyItemChanged(selectedPosition) }
                viewBinding.root.backgroundTintList = selectedBackgroundTint

                selectedBankId = bankList[position].uid
                selectedPosition = position
                bankOnClickedListener.onClicked(bankList[position], position)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(BankItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = bankList.size

}