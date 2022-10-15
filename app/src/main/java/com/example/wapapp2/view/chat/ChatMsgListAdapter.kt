package com.example.wapapp2.view.chat

import android.graphics.Color
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.databinding.ChatMsgItemBinding

class ChatMsgListAdapter {

    private inner class ViewHolder(val binding: ChatMsgItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val position = adapterPosition

        }

        private fun configMessage() {
            configText()
            configUsername()
            configSpacing()
        }

        private fun configText() {
            val text = binding.msg

            val params = text.layoutParams as ConstraintLayout.LayoutParams
            if (messageListItem.isMine) {
                params.horizontalBias = 1f
                text.setTextColor(Color.WHITE)
                text.setPadding(dpToPixel(10f), dpToPixel(5f), dpToPixel(15f), dpToPixel(5f))
                if (isBottom()) {
                    text.setBackgroundResource(R.drawable.chat_msg_item_bubble_right_tail)
                } else {
                    text.setBackgroundResource(R.drawable.chat_msg_bubble_right)
                }
            } else {
                params.horizontalBias = 0f
                text.setTextColor(Color.BLACK)
                text.setPadding(dpToPixel(15f), dpToPixel(5f), dpToPixel(10f), dpToPixel(5f))
                if (isBottom()) {
                    text.setBackgroundResource(R.drawable.chat_msg_item_bubble_left_tail)
                } else {
                    text.setBackgroundResource(R.drawable.chat_msg_bubble_left)
                }
            }

        }

        private fun configUsername() {
            if (channelState.members.size == 2 || messageListItem.isMine || !isTop()) {
                binding.userName.visibility = View.GONE
                return
            }

            binding.userName.text = messageListItem.message.user.name

            val params = binding.userName.layoutParams as ConstraintLayout.LayoutParams
            if (messageListItem.isMine) {
                params.horizontalBias = 1f
                params.rightMargin = 40
            } else {
                params.horizontalBias = 0f
                params.leftMargin = 40
            }
        }

        private fun configSpacing() {
            if (!isTop()) {
                binding.spaceHeader.layoutParams.height = 5
            }
        }

        private fun configDate() {
            binding.userName.visibility = View.GONE
            binding.msg.text = DateFormat
                    .getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(messageListItem.date)
        }
    }
}