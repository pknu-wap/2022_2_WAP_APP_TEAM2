package com.example.wapapp2.view.receipt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.SimpleReceiptItemBinding
import com.example.wapapp2.model.ReceiptDTO
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SideNavSimpleReceiptsAdapter(
        private val onClickListener: ListOnClickListener<ReceiptDTO>, options: FirestoreRecyclerOptions<ReceiptDTO>,
) :
        FirestoreRecyclerAdapter<ReceiptDTO, SideNavSimpleReceiptsAdapter.CustomViewHolder>(options), IAdapterItemCount {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CustomViewHolder(SimpleReceiptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickListener)

    class CustomViewHolder(
            private val binding: SimpleReceiptItemBinding,
            private val onClickListener: ListOnClickListener<ReceiptDTO>,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(receiptDTO: ReceiptDTO) {
            if (receiptDTO.imgUrl.isEmpty()) {
                Glide.with(binding.root).load(R.drawable.ic_baseline_receipt_24).into(binding.root)
            } else {
                val storageReference = Firebase.storage.getReferenceFromUrl(receiptDTO.imgUrl)
                Glide.with(binding.root).load(storageReference).into(binding.root)
            }

            binding.root.setOnClickListener {
                onClickListener.onClicked(receiptDTO, bindingAdapterPosition)
            }


        }

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int, model: ReceiptDTO) {
        holder.bind(model)
    }

    override fun getAdapterItemCount(): Int = itemCount

}