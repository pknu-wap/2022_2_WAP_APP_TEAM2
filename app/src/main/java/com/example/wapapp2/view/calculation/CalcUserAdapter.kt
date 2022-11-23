package com.example.wapapp2.view.calculation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.ChatFriendsItemBinding
import com.example.wapapp2.view.login.Profiles


class CalcUserAdapter(val context: Context?, val items: ArrayList<Profiles>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FriendsVH(val binding: ChatFriendsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Profiles) {
            binding.profileImg.setImageDrawable(
                ContextCompat.getDrawable(context!!, item.gender)
            )
            binding.friendName.text = item.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return FriendsVH(ChatFriendsItemBinding.inflate(LayoutInflater.from(context)))

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FriendsVH).bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
