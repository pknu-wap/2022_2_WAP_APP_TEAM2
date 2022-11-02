package com.example.wapapp2.view.calculation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.view.login.ProfileAdapter


class EditReceiptAdapter(val itemList: ArrayList<EditReceipt>) : RecyclerView.Adapter<EditReceiptAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditReceiptAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.edit_calc, parent, false)
        return EditReceiptAdapter.CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.name.text = itemList.get(position).name
        holder.amount.text = itemList.get(position).amount.toString()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.receipt_item)
        val amount = itemView.findViewById<TextView>(R.id.receipt_amount)
    }
}