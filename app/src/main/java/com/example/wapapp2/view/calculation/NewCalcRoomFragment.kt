package com.example.wapapp2.view.calculation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.CheckedFriendItemviewBinding
import com.example.wapapp2.databinding.FragmentNewCalcRoomBinding
import com.example.wapapp2.databinding.SearchFriendItemViewBinding
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.viewmodel.NewCalcRoomViewModel
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.material.checkbox.MaterialCheckBox


class NewCalcRoomFragment : Fragment() {
    private lateinit var binding: FragmentNewCalcRoomBinding

    private val calcRoomViewModel: NewCalcRoomViewModel by viewModels()
    private val searchFriendsListAdapter: SearchFriendsListAdapter = SearchFriendsListAdapter()
    private val checkedFriendsListAdapter: CheckedFriendsListAdapter = CheckedFriendsListAdapter()

    private val onCheckedFriendListener: OnCheckedFriendListener = object : OnCheckedFriendListener {
        override fun onChecked(isChecked: Boolean, friendDTO: FriendDTO) {
            calcRoomViewModel.checkedFriend(friendDTO, isChecked)
        }
    }

    private val onRemovedFriendListener: OnRemovedFriendListener = object : OnRemovedFriendListener {
        override fun onRemoved(friendDTO: FriendDTO) {
            calcRoomViewModel.checkedFriend(friendDTO, false)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkedFriendsListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                changeViewState()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                changeViewState()
            }

            fun changeViewState() {
                if (checkedFriendsListAdapter.itemCount == 0) {
                    binding.inviteFriendsList.visibility = View.GONE
                } else {
                    binding.inviteFriendsList.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewCalcRoomBinding.inflate(inflater)

        binding.inviteFriendsList.adapter = checkedFriendsListAdapter
        binding.searchFriendsList.adapter = searchFriendsListAdapter

        binding.inviteFriendsList.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calcRoomViewModel.friendCheckedLiveData.observe(viewLifecycleOwner) {
            if (it.isChecked) {
                checkedFriendsListAdapter.addItem(it.friendDTO)
            } else {
                checkedFriendsListAdapter.removeItem(it.friendDTO)
                searchFriendsListAdapter.uncheckItem(it.friendDTO)
            }
        }

        calcRoomViewModel.searchResultFriendsLiveData.observe(viewLifecycleOwner) {
            searchFriendsListAdapter.setList(it)
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.apply { calcRoomViewModel.getFriends(toString()) }
            }
        })
        calcRoomViewModel.getFriends("")
    }

    inner class SearchFriendsListAdapter : RecyclerView.Adapter<SearchFriendsListAdapter.ViewHolder>() {
        private val friendsList = ArrayList<FriendDTO>()
        private val checkedSet = HashSet<String>()
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
            notifyDataSetChanged()
        }

        fun uncheckItem(friendDTO: FriendDTO) {
            checkedSet.remove(friendDTO.uid)
            notifyDataSetChanged()
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

    inner class CheckedFriendsListAdapter : RecyclerView.Adapter<CheckedFriendsListAdapter.ViewHolder>() {
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
                if (value.uid == friendDTO.uid) {
                    list.removeAt(index)
                    notifyItemRemoved(index)
                    break
                }
            }


        }
    }

    interface OnCheckedFriendListener {
        fun onChecked(isChecked: Boolean, friendDTO: FriendDTO)
    }

    interface OnRemovedFriendListener {
        fun onRemoved(friendDTO: FriendDTO)
    }
}