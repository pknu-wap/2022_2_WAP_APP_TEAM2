package com.example.wapapp2.view.friends.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.UserSearchResultItemViewBinding
import com.example.wapapp2.model.UserDTO

class SearchUserListAdapter(private val listOnClickListener: ListOnClickListener<UserDTO>) :
        RecyclerView.Adapter<SearchUserListAdapter.ViewHolder>() {
    private val _users = mutableListOf<UserDTO>()
    var users = _users
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    class ViewHolder(private val itemBinding: UserSearchResultItemViewBinding,
                     private val listOnClickListener: ListOnClickListener<UserDTO>) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(userDTO: UserDTO) {
            itemBinding.userEmail.text = userDTO.email
            itemBinding.userName.text = userDTO.name
            itemBinding.addBtn.setOnClickListener {
                listOnClickListener.onClicked(userDTO, adapterPosition)
            }
        }


    }


    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(UserSearchResultItemViewBinding.inflate
            (LayoutInflater.from(parent.context), parent, false), listOnClickListener)


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = _users.size

}