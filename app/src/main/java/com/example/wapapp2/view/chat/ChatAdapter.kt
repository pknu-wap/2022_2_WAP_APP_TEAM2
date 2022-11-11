package com.example.wapapp2.view.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DateConverter
import com.example.wapapp2.databinding.ChatMsgItemBinding
import com.example.wapapp2.model.ChatDTO
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat

class ChatAdapter(val myId: String, option : FirestoreRecyclerOptions<ChatDTO>) : FirestoreRecyclerAdapter<ChatDTO, ChatAdapter.ChatHolder>(option) {
    private val timeFormat = DateTimeFormat.forPattern("a hh:mm")
    private val dateTimeParser = ISODateTimeFormat.dateTimeParser()


    inner class ChatHolder(val binding: ChatMsgItemBinding) : RecyclerView.ViewHolder(binding.root){
        var time = DateTime()

        fun bind(position: Int, model : ChatDTO) {
            binding.root.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val msgParams = binding.msg.layoutParams as ConstraintLayout.LayoutParams
            val userNameParams = binding.userName.layoutParams as ConstraintLayout.LayoutParams
            val timeParams = binding.time.layoutParams as ConstraintLayout.LayoutParams

            binding.msg.text = model.msg

            if (model.senderId == myId) {
                //내 메시지
                binding.msg.setTextColor(Color.WHITE)
                binding.msg.setBackgroundResource(R.drawable.chat_msg_bubble_right)

                userNameParams.endToEnd = R.id.parent
                msgParams.endToEnd = R.id.parent
                timeParams.endToStart = R.id.msg
            } else {
                //상대방 메시지
                binding.msg.setTextColor(Color.BLACK)
                binding.msg.setBackgroundResource(R.drawable.chat_msg_bubble_left)

                userNameParams.startToStart = R.id.parent
                msgParams.startToStart = R.id.parent
                timeParams.startToEnd = R.id.msg
            }

            binding.msg.layoutParams = msgParams
            binding.userName.layoutParams = userNameParams
            binding.time.layoutParams = timeParams


            time = dateTimeParser.parseDateTime(DateConverter.toISO8601(model.sendedTime!!))
            binding.time.text = timeFormat.print(time)
            setUserName(position, model)
            }

        private fun setUserName(position: Int , model: ChatDTO) {
            if ( model.senderId == myId || equalsTopUserId(model.userId, position)) {
                binding.userName.visibility = View.GONE
                return
            }
            binding.userName.visibility = View.VISIBLE
            binding.userName.text = model.userName
        }


        private fun equalsTopUserId(userId: String, currentPosition: Int): Boolean {
            return if (currentPosition - 1 < 0) false else snapshots[currentPosition - 1].senderId == userId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatHolder {
        return ChatHolder(ChatMsgItemBinding.inflate(LayoutInflater.from(parent.context)))
    }


    override fun onBindViewHolder(holder: ChatHolder, position: Int, model: ChatDTO) {
        holder.bind(position, model)
    }



}