package com.example.wapapp2.view.friends.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.SearchFriendItemViewBinding
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.view.friends.interfaces.OnCheckedFriendListener
import com.google.android.material.checkbox.MaterialCheckBox

class SearchFriendsListAdapter(private val onCheckedFriendListener: OnCheckedFriendListener) :
        RecyclerView.Adapter<SearchFriendsListAdapter.ViewHolder>() {
    private val friendsList = mutableListOf<FriendDTO>()
    private val checkedSet = HashSet<String>()
    private val ignoreIdSet = mutableSetOf<String>()
    private var onCheckedStateChangedListener: MaterialCheckBox.OnCheckedStateChangedListener? = null

    inner class ViewHolder(private val itemBinding: SearchFriendItemViewBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind() {
            reset()

            val position = bindingAdapterPosition
            val friendDTO = friendsList[position]

            itemBinding.friendName.text = friendDTO.alias

            if (checkedSet.contains(friendDTO.friendUserId))
                itemBinding.checkbox.checkedState = MaterialCheckBox.STATE_CHECKED

            onCheckedStateChangedListener = MaterialCheckBox.OnCheckedStateChangedListener { checkBox, state ->
                var conflict: Boolean = false

                if (state == MaterialCheckBox.STATE_CHECKED) {
                    if (checkedSet.contains(friendDTO.friendUserId))
                        conflict = true
                    else
                        checkedSet.add(friendDTO.friendUserId)
                } else {
                    if (!checkedSet.contains(friendDTO.friendUserId))
                        conflict = true
                    else
                        checkedSet.remove(friendDTO.friendUserId)
                }

                if (!conflict)
                    onCheckedFriendListener.onChecked(state == MaterialCheckBox.STATE_CHECKED, friendDTO)
            }

            itemBinding.checkbox.addOnCheckedStateChangedListener(onCheckedStateChangedListener!!)
        }

        fun reset() {
            itemBinding.checkbox.clearOnCheckedStateChangedListeners()
            if (onCheckedStateChangedListener != null) {
                itemBinding.checkbox.removeOnCheckedStateChangedListener(onCheckedStateChangedListener!!)
                onCheckedStateChangedListener = null
            }
            itemBinding.checkbox.checkedState = MaterialCheckBox.STATE_UNCHECKED
        }
    }


    fun setList(list: MutableList<FriendDTO>) {
        friendsList.clear()
        friendsList.addAll(list)

        if (ignoreIdSet.isNotEmpty()) {
            val removeIdxs = mutableListOf<Int>()
            for (idx in friendsList.size - 1 downTo 0) {
                if (ignoreIdSet.contains(friendsList[idx].friendUserId)) {
                    removeIdxs.add(idx)
                }
            }

            for (idx in removeIdxs) {
                friendsList.removeAt(idx)
            }
        }

        notifyDataSetChanged()
    }

    fun uncheckItem(friendDTO: FriendDTO) {
        checkedSet.remove(friendDTO.friendUserId)
        notifyDataSetChanged()
    }


    fun ignoreIds(set: MutableSet<String>) {
        ignoreIdSet.clear()
        ignoreIdSet.addAll(set)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.reset()
        super.onViewRecycled(holder)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SearchFriendItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = friendsList.size

}