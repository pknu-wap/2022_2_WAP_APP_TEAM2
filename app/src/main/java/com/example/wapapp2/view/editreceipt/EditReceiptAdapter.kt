package com.example.wapapp2.view.editreceipt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.view.checkreceipt.CheckReceiptAdapter

class EditReceiptAdapter (val itemList: ArrayList<ItemList>) : RecyclerView.Adapter<EditReceiptAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.edit_receipt_detail, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditReceiptAdapter.CustomViewHolder, position: Int) {
        holder.name.text = itemList.get(position).name
        holder.amount.text = itemList.get(position).amount
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.edit_name)
        val amount = itemView.findViewById<TextView>(R.id.edit_amount)
    }
}