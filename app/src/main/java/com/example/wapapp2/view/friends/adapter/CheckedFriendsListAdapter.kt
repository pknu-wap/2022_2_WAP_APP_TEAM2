package com.example.wapapp2.view.friends.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.CheckedFriendItemviewBinding
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.view.friends.interfaces.OnRemovedFriendListener
import okhttp3.internal.notify

class CheckedFriendsListAdapter(private val onRemovedFriendListener: OnRemovedFriendListener) :
        RecyclerView.Adapter<CheckedFriendsListAdapter.ViewHolder>() {
    private val list = ArrayList<FriendDTO>()
    val friends get() = list

    inner class ViewHolder(private val itemBinding: CheckedFriendItemviewBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind() {
            val position = bindingAdapterPosition

            val friendDTO = list[position]
            itemBinding.friendName.text = friendDTO.alias
            itemBinding.removeBtn.setOnClickListener {
                onRemovedFriendListener.onRemoved(friendDTO)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CheckedFriendItemviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = list.size

    fun addItem(friendDTO: FriendDTO) {
        list.add(friendDTO)
        notifyItemInserted(list.size - 1)
    }

    fun removeItem(friendDTO: FriendDTO) {
        for ((index, value) in list.withIndex()) {
            if (value.friendUserId == friendDTO.friendUserId) {
                list.removeAt(index)
                notifyItemRemoved(index)
                break
            }
        }
    }

    fun resetItem(){
        val count = list.size
        list.clear()
        notifyItemRangeRemoved(0,count)
        
        //미완료 -> 수정필요
    }

    fun getParticipantIDs(myId : String) : ArrayList<String>{
        val result = arrayListOf<String>(myId)
        friends.forEach { result.add(it.friendUserId) }
        return result
    }
}