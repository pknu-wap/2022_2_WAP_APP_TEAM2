package com.example.wapapp2.view.friends.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.MyFriendItemViewBinding
import com.example.wapapp2.model.FriendDTO
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.DocumentSnapshot

class MyFriendsAdapter(
        private val onClickListener: ListOnClickListener<FriendDTO>,
        options: FirestoreRecyclerOptions<FriendDTO>,
) :
        FirestoreRecyclerAdapter<FriendDTO, MyFriendsAdapter.CustomViewHolder>(options), IAdapterItemCount {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(MyFriendItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickListener)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int, model: FriendDTO) {
        holder.bind(model)
    }

    override fun onDataChanged() {
        super.onDataChanged()
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
        super.onChildChanged(type, snapshot, newIndex, oldIndex)
    }

    class CustomViewHolder(
            private val binding: MyFriendItemViewBinding,
            private val onClickListener: ListOnClickListener<FriendDTO>,
    ) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(friendDTO: FriendDTO) {
            binding.myAccountId.text = "email"
            binding.myProfileName.text = friendDTO.alias

            binding.root.setOnClickListener {
                onClickListener.onClicked(friendDTO, bindingAdapterPosition)
            }
        }

    }

    override fun getAdapterItemCount(): Int {
        return itemCount
    }
}