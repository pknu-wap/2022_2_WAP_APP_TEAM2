package com.example.wapapp2.view.friends.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.CheckedFriendItemviewBinding
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.view.friends.interfaces.OnRemovedFriendListener

class CheckedFriendsListAdapter(private val onRemovedFriendListener: OnRemovedFriendListener) : RecyclerView.Adapter<CheckedFriendsListAdapter.ViewHolder>() {
    private val list = ArrayList<FriendDTO>()

    inner class ViewHolder(private val itemBinding: CheckedFriendItemviewBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind() {
            val position = adapterPosition

            val friendDTO = list[position]
            itemBinding.friendName.text = friendDTO.friendName
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
}