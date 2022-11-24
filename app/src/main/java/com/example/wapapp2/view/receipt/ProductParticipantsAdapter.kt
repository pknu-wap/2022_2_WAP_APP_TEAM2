package com.example.wapapp2.view.receipt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.ParticipantNameViewBinding
import com.example.wapapp2.model.ReceiptProductParticipantDTO

class ProductParticipantsAdapter(private val listOnClickListener: ListOnClickListener<ReceiptProductParticipantDTO>) : RecyclerView
.Adapter<ProductParticipantsAdapter
.ViewHolder>() {
    val participants = mutableListOf<ReceiptProductParticipantDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ParticipantNameViewBinding.inflate(LayoutInflater.from(parent.context),
                    parent, false), listOnClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(participants[position])
    }

    override fun getItemCount(): Int = participants.size

    class ViewHolder(
            private val binding: ParticipantNameViewBinding,
            private val listOnClickListener: ListOnClickListener<ReceiptProductParticipantDTO>,
    ) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ReceiptProductParticipantDTO) {
            binding.root.setOnClickListener {
                listOnClickListener.onClicked(model, bindingAdapterPosition)
            }

            binding.name.text = model.userName
        }
    }
}