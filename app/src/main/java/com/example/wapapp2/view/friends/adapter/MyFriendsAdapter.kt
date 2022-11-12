package com.example.wapapp2.view.friends.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.MyFriendItemViewBinding
import com.example.wapapp2.model.FriendDTO
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions

class MyFriendsAdapter(private val onClickListener: ListOnClickListener<FriendDTO>,
                       options: FirestoreRecyclerOptions<FriendDTO>) :
        FirestoreRecyclerAdapter<FriendDTO, MyFriendsAdapter.CustomViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(MyFriendItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickListener)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int, model: FriendDTO) {
        holder.bind(model)
    }


    class CustomViewHolder(private val binding: MyFriendItemViewBinding,
                           private val onClickListener: ListOnClickListener<FriendDTO>) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(friendDTO: FriendDTO) {
            binding.userId1.text = friendDTO.email
            binding.userName1.text = friendDTO.alias

            binding.root.setOnClickListener {
                onClickListener.onClicked(friendDTO, bindingAdapterPosition)
            }
        }

    }
}