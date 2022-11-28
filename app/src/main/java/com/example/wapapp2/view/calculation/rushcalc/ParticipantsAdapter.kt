package com.example.wapapp2.view.calculation.rushcalc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.MyCheckBoxListener
import com.example.wapapp2.databinding.ParticipantItemViewBinding
import com.example.wapapp2.model.CalcRoomParticipantDTO
import com.example.wapapp2.viewmodel.CalculationViewModel

class ParticipantsAdapter(private val iCheckedRecipient: ICheckedRecipient<CalcRoomParticipantDTO>) :
        RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {

    val participants = mutableListOf<CalcRoomParticipantDTO>()


    class ViewHolder(
            private val binding: ParticipantItemViewBinding,
            private val iCheckedRecipient: ICheckedRecipient<CalcRoomParticipantDTO>,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var myCheckBoxListener: MyCheckBoxListener<CalcRoomParticipantDTO>? = null

        fun bind(participantDTO: CalcRoomParticipantDTO) {
            binding.friendCheckbox.text = participantDTO.userName

            myCheckBoxListener?.run {
                binding.friendCheckbox.removeOnCheckedStateChangedListener(this)
                null
            }

            myCheckBoxListener = object : MyCheckBoxListener<CalcRoomParticipantDTO>(participantDTO) {
                override fun onCheckedChanged(e: CalcRoomParticipantDTO, isChecked: Boolean) {
                    iCheckedRecipient.onCheckedChange(e, isChecked)
                }
            }
            binding.friendCheckbox.addOnCheckedStateChangedListener(myCheckBoxListener!!)
        }
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
    ): ViewHolder = ViewHolder(ParticipantItemViewBinding.inflate(LayoutInflater.from(
            parent.context), parent, false), iCheckedRecipient)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(participants[position])
    }

    override fun getItemCount(): Int = participants.size
}