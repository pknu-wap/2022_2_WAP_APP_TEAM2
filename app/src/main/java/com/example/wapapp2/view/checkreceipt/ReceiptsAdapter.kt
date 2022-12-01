package com.example.wapapp2.view.checkreceipt

import android.annotation.SuppressLint
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wapapp2.commons.classes.DataTypeConverter
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.commons.interfaces.ListOnLongClickListener
import com.example.wapapp2.databinding.CheckReceiptBinding
import com.example.wapapp2.model.ReceiptDTO
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

class ReceiptsAdapter(
        private val onClickListener: ListOnClickListener<ReceiptDTO>,
        private val onLongClickListener: ListOnLongClickListener<ReceiptDTO>, options: FirestoreRecyclerOptions<ReceiptDTO>,
) :
        FirestoreRecyclerAdapter<ReceiptDTO, ReceiptsAdapter.CustomViewHolder>(options), IAdapterItemCount {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CustomViewHolder(CheckReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickListener,
                    onLongClickListener)


    class CustomViewHolder(
            private val binding: CheckReceiptBinding,
            private val onClickListener: ListOnClickListener<ReceiptDTO>,
            private val onLongClickListener: ListOnLongClickListener<ReceiptDTO>,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd E a hh:mm", Locale.getDefault())

        fun bind(receiptDTO: ReceiptDTO) {
            binding.receiptDate.text = simpleDateFormat.format(receiptDTO.createdTime!!)
            binding.receiptTitle.text = receiptDTO.name.ifEmpty { "제목 없음" }
            binding.totalMoney.text = DataTypeConverter.toKRW(receiptDTO.totalMoney)

            if (receiptDTO.imgUrl.isNullOrEmpty()) {
                Glide.with(binding.root).clear(binding.receiptImage)
            } else {
                val storageReference = Firebase.storage.getReferenceFromUrl(receiptDTO.imgUrl!!)
                Glide.with(binding.root).load(storageReference).into(binding.receiptImage)
            }

            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "총 금액 : ${receiptDTO.totalMoney}\n영수증 이름 : ${receiptDTO.name}", Toast
                        .LENGTH_SHORT
                ).show()

                onClickListener.onClicked(receiptDTO, bindingAdapterPosition)
            }

            binding.root.setOnLongClickListener {
                onLongClickListener.onLongClicked(receiptDTO, bindingAdapterPosition)
                true
            }

        }

        private fun String.toEditable(): Editable = Editable.Factory().newEditable(this)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int, model: ReceiptDTO) {
        holder.bind(model)
    }

    override fun getAdapterItemCount(): Int = itemCount

}