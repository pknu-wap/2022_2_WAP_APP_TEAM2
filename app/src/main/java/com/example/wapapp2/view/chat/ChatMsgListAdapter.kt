package com.example.wapapp2.view.chat

import android.content.Context
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
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.format.ISODateTimeFormat.date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatMsgListAdapter(context: Context, val myId: String) : RecyclerView.Adapter<ChatMsgListAdapter.ViewHolder>() {
    private val layoutInflater: LayoutInflater
    private val timeFormat = DateTimeFormat.forPattern("a hh:mm")
    private val dateTimeParser = ISODateTimeFormat.dateTimeParser()
    val chatList = ArrayList<ChatDTO>()

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    inner class ViewHolder(val binding: ChatMsgItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var time = DateTime()

        fun bind() {
            binding.root.layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val position = adapterPosition
            val msgParams = binding.msg.layoutParams as ConstraintLayout.LayoutParams
            val userNameParams = binding.userName.layoutParams as ConstraintLayout.LayoutParams
            val timeParams = binding.time.layoutParams as ConstraintLayout.LayoutParams

            binding.msg.text = chatList[position].msg

            if (chatList[position].userId == myId) {
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

            setUserName(position)

            if (equalsTopUserId(chatList[position].userId, position)) {
                binding.spaceHeader.layoutParams.height = 6
            }


            time = dateTimeParser.parseDateTime(DateConverter.toISO8601(chatList[position].sendedTime!!))
            binding.time.text = timeFormat.print(time)
        }


        private fun setUserName(position: Int) {
            if (chatList[position].userId == myId || equalsTopUserId(chatList[position].userId, position)) {
                binding.userName.visibility = View.GONE
                return
            }

            binding.userName.visibility = View.VISIBLE
            binding.userName.text = chatList[position].userName
        }


        private fun equalsTopUserId(userId: String, currentPosition: Int): Boolean {
            return if (currentPosition - 1 < 0) false else chatList[currentPosition - 1].userId == userId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ChatMsgItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = chatList.size
}