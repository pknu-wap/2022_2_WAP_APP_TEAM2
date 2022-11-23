package com.example.wapapp2.view.calculation.calcroom.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.ChatFriendsItemBinding
import com.example.wapapp2.model.CalcRoomParticipantDTO


class ParticipantsInCalcRoomAdapter(private val listOnClickListener: ListOnClickListener<CalcRoomParticipantDTO>)
    : RecyclerView.Adapter<ParticipantsInCalcRoomAdapter.ViewHolder>() {

    private val _participants = mutableListOf<CalcRoomParticipantDTO>()
    var participants = _participants
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    class ViewHolder(
            private val binding: ChatFriendsItemBinding,
            private val listOnClickListener: ListOnClickListener<CalcRoomParticipantDTO>,
    ) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalcRoomParticipantDTO) {
            binding.friendName.text = item.userName
            binding.root.setOnClickListener {
                listOnClickListener.onClicked(item, bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ChatFriendsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), listOnClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(participants[position])
    }

    override fun getItemCount() = participants.size
}