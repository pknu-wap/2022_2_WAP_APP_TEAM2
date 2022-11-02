package com.example.wapapp2.view.friends.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.SearchFriendItemViewBinding
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.view.friends.interfaces.OnCheckedFriendListener
import com.google.android.material.checkbox.MaterialCheckBox

class SearchFriendsListAdapter(private val onCheckedFriendListener: OnCheckedFriendListener) : RecyclerView.Adapter<SearchFriendsListAdapter.ViewHolder>() {
    private val friendsList = ArrayList<FriendDTO>()
    private val checkedSet = HashSet<String>()
    private val ignoreIdSet: HashSet<String> = HashSet<String>()
    private var onCheckedStateChangedListener: MaterialCheckBox.OnCheckedStateChangedListener? = null

    inner class ViewHolder(private val itemBinding: SearchFriendItemViewBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind() {
            reset()

            val position = adapterPosition
            val friendDTO = friendsList[position]

            itemBinding.friendName.text = friendDTO.friendName

            if (checkedSet.contains(friendDTO.uid))
                itemBinding.checkbox.checkedState = MaterialCheckBox.STATE_CHECKED

            onCheckedStateChangedListener = MaterialCheckBox.OnCheckedStateChangedListener { checkBox, state ->
                var conflict: Boolean = false

                if (state == MaterialCheckBox.STATE_CHECKED) {
                    if (checkedSet.contains(friendDTO.uid))
                        conflict = true
                    else
                        checkedSet.add(friendDTO.uid)
                } else {
                    if (!checkedSet.contains(friendDTO.uid))
                        conflict = true
                    else
                        checkedSet.remove(friendDTO.uid)
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


    fun setList(list: ArrayList<FriendDTO>) {
        friendsList.clear()
        friendsList.addAll(list)

        if (ignoreIdSet.isNotEmpty()) {
            val removeIdxs = ArrayList<Int>()
            for (idx in friendsList.size - 1..0) {
                if (ignoreIdSet.contains(friendsList[idx].uid)) {
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
        checkedSet.remove(friendDTO.uid)
        notifyDataSetChanged()
    }


    fun ignoreIds(set: HashSet<String>) {
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