package com.example.wapapp2.view.checkreceipt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener

class CheckReceiptAdapter (val receiptList: ArrayList<ReceiptList>, private val onClickListener: ListOnClickListener<ReceiptList>) : RecyclerView
.Adapter<CheckReceiptAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.check_receipt, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {
                val curPos: Int = adapterPosition
                val receipt: ReceiptList = receiptList.get(curPos)
                Toast.makeText(
                    parent.context,
                    "금액 : ${receipt.amount}\n타이틀 : ${receipt.title})",
                    Toast.LENGTH_SHORT
                ).show()

                onClickListener.onClicked(receiptList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.amount.text = receiptList.get(position).amount
        holder.title.text = receiptList.get(position).title
        holder.date.text = receiptList.get(position).date
        holder.image.setImageResource(receiptList.get(position).receipt)
    }

    override fun getItemCount(): Int {
        return receiptList.size
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount = itemView.findViewById<TextView>(R.id.receipt_amount)
        val title = itemView.findViewById<TextView>(R.id.receipt_title)
        val date = itemView.findViewById<TextView>(R.id.receipt_date)
        val image = itemView.findViewById<ImageView>(R.id.receipt_image)
    }
}