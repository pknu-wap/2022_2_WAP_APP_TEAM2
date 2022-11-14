package com.example.wapapp2.view.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.DateConverter
import com.example.wapapp2.databinding.ChatMsgItemReceivedBinding
import com.example.wapapp2.databinding.ChatMsgItemSendedBinding
import com.example.wapapp2.model.ChatDTO
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat

class ChatAdapter(val myId: String, option : FirestoreRecyclerOptions<ChatDTO>, val scrollListener: ScrollListener) : FirestoreRecyclerAdapter<ChatDTO, RecyclerView.ViewHolder>(option){
    private val timeFormat = DateTimeFormat.forPattern("a hh:mm")
    private val dateTimeParser = ISODateTimeFormat.dateTimeParser()


    private enum class ItemViewType {
        SENDED, RECEVEIED
    }

    inner class ChatHolder_sended(val binding: ChatMsgItemSendedBinding) : RecyclerView.ViewHolder(binding.root), ChatHolder{
        var time = DateTime()

        override fun bind(position: Int, model : ChatDTO) {
            binding.msg.text = model.msg
            time = dateTimeParser.parseDateTime(DateConverter.toISO8601(model.sendedTime!!))
            binding.time.text = timeFormat.print(time)
        }
    }


    inner class ChatHolder_received(val binding: ChatMsgItemReceivedBinding) : RecyclerView.ViewHolder(binding.root), ChatHolder{
        var time = DateTime()

        override fun bind(position: Int, model : ChatDTO) {
            binding.msg.text = model.msg
            time = dateTimeParser.parseDateTime(DateConverter.toISO8601(model.sendedTime!!))
            binding.time.text = timeFormat.print(time)
            setUserName(position, model)
            }

        private fun setUserName(position: Int , model: ChatDTO) {
            if ( model.senderId == myId || equalsTopUserId(model.senderId, position)) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return if(viewType == ItemViewType.RECEVEIED.ordinal) ChatHolder_received(ChatMsgItemReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)) as RecyclerView.ViewHolder else ChatHolder_sended(ChatMsgItemSendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)) as RecyclerView.ViewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return if(snapshots[position].senderId == myId) ItemViewType.SENDED.ordinal else ItemViewType.RECEVEIED.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: ChatDTO) {
        (holder as ChatHolder).bind(position, model)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        scrollListener.ScrollToBottom()
    }

}