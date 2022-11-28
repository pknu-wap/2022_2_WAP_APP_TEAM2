package com.example.wapapp2.view.friends.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.IAdapterItemCount
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.MyFriendItemViewBinding
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.model.UserDTO
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFriendsAdapter(
        private val onClickListener: ListOnClickListener<FriendDTO>,
        options: FirestoreRecyclerOptions<UserDTO>,
        val friendMap: Map<String, FriendDTO>
) :
        FirestoreRecyclerAdapter<UserDTO, MyFriendsAdapter.CustomViewHolder>(options), IAdapterItemCount {

    lateinit var defaultImgMan : Drawable
    lateinit var defaultImgGirl : Drawable


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        defaultImgMan = ContextCompat.getDrawable(parent.context, R.drawable.man)!!
        defaultImgGirl = ContextCompat.getDrawable(parent.context, R.drawable.girl)!!
        return CustomViewHolder(MyFriendItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickListener)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int, model: UserDTO) {
        holder.bind(model)
    }

    override fun onDataChanged() {
        super.onDataChanged()
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
        super.onChildChanged(type, snapshot, newIndex, oldIndex)
    }

    inner class CustomViewHolder(
            private val binding: MyFriendItemViewBinding,
            private val onClickListener: ListOnClickListener<FriendDTO>,
    ) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(userDTO : UserDTO) {
            binding.myAccountId.text = userDTO.email
            binding.myProfileName.text = userDTO.name

            friendMap[userDTO.id]?.apply {
                binding.myProfileName.text = this.alias
                binding.root.setOnClickListener {
                    onClickListener.onClicked(this, bindingAdapterPosition)
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                if(userDTO.imgUri.isEmpty().not()) Glide.with(binding.root).load(userDTO.imgUri).circleCrop().into(binding.myProfileImg)
                else if (userDTO.gender == "man") binding.myProfileImg.setImageDrawable(defaultImgMan)
                else binding.myProfileImg.setImageDrawable(defaultImgGirl)
            }

        }



    }

    override fun getAdapterItemCount(): Int {
        return itemCount
    }
}