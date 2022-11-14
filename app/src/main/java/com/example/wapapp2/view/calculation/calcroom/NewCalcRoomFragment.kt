package com.example.wapapp2.view.calculation.calcroom

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.FragmentNewCalcRoomBinding
import com.example.wapapp2.view.friends.adapter.CheckedFriendsListAdapter
import com.example.wapapp2.view.friends.adapter.SearchFriendsListAdapter
import com.example.wapapp2.view.friends.interfaces.OnCheckedFriendListener
import com.example.wapapp2.view.friends.interfaces.OnRemovedFriendListener
import com.example.wapapp2.viewmodel.FriendsViewModel


class NewCalcRoomFragment : Fragment() {
    private lateinit var binding: FragmentNewCalcRoomBinding

    companion object {
        const val TAG = "NewCalcRoomFragment"
    }

    private val calcRoomViewModel: FriendsViewModel by viewModels()

    private val onCheckedFriendListener: OnCheckedFriendListener =
            OnCheckedFriendListener { isChecked, friendDTO -> calcRoomViewModel.checkedFriend(friendDTO, isChecked) }
    private val onRemovedFriendListener: OnRemovedFriendListener =
            OnRemovedFriendListener { friendDTO -> calcRoomViewModel.checkedFriend(friendDTO, false) }

    private val searchFriendsListAdapter: SearchFriendsListAdapter = SearchFriendsListAdapter(onCheckedFriendListener)
    private val checkedFriendsListAdapter: CheckedFriendsListAdapter = CheckedFriendsListAdapter(onRemovedFriendListener)

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
                    binding.inviteFriendsLayout.inviteFriendsList.visibility = View.GONE
                } else {
                    binding.inviteFriendsLayout.inviteFriendsList.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewCalcRoomBinding.inflate(inflater)

        binding.inviteFriendsLayout.inviteFriendsList.adapter = checkedFriendsListAdapter
        binding.inviteFriendsLayout.searchFriendsList.adapter = searchFriendsListAdapter

        binding.inviteFriendsLayout.inviteFriendsList.visibility = View.GONE

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

        binding.inviteFriendsLayout.searchBar.addTextChangedListener(object : TextWatcher {
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

}