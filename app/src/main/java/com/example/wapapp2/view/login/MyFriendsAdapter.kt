package com.example.wapapp2.view.login

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.MyFriendItemViewBinding
import com.example.wapapp2.model.FriendDTO

class MyFriendsAdapter(private val onClickListener: ListOnClickListener<FriendDTO>) :
        RecyclerView
        .Adapter<MyFriendsAdapter.CustomViewHolder>() {
    private val _friends: MutableList<FriendDTO> = mutableListOf()
    var friends = _friends
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            CustomViewHolder(MyFriendItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickListener)

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(friends[position])
    }

    override fun getItemCount() = friends.size


    class CustomViewHolder(private val binding: MyFriendItemViewBinding, private val onClickListener: ListOnClickListener<FriendDTO>) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(friendDTO: FriendDTO) {
            binding.userId1.text = friendDTO.email
            binding.userName1.text = friendDTO.alias

            binding.root.setOnClickListener {
                onClickListener.onClicked(friendDTO, adapterPosition)
            }
        }

    }
}