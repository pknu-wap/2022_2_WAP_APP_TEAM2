package com.example.wapapp2.view.friends

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.FragmentInviteFriendsBinding
import com.example.wapapp2.databinding.FragmentNewCalcRoomBinding
import com.example.wapapp2.view.friends.adapter.CheckedFriendsListAdapter
import com.example.wapapp2.view.friends.adapter.SearchFriendsListAdapter
import com.example.wapapp2.view.friends.interfaces.OnCheckedFriendListener
import com.example.wapapp2.view.friends.interfaces.OnRemovedFriendListener
import com.example.wapapp2.viewmodel.FriendsViewModel
import kotlin.collections.HashSet


class InviteFriendsFragment : Fragment() {
    private lateinit var binding: FragmentInviteFriendsBinding

    private val friendsViewModel: FriendsViewModel by viewModels()

    private val onCheckedFriendListener: OnCheckedFriendListener = OnCheckedFriendListener { isChecked, friendDTO -> friendsViewModel.checkedFriend(friendDTO, isChecked) }
    private val onRemovedFriendListener: OnRemovedFriendListener = OnRemovedFriendListener { friendDTO -> friendsViewModel.checkedFriend(friendDTO, false) }

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
                binding.inviteFriendsLayout.inviteFriendsList.visibility = if (checkedFriendsListAdapter.itemCount == 0) View.GONE else
                    View.VISIBLE
            }
        })

        val bundle = arguments ?: savedInstanceState
        val currentFriendsInRoomSet: HashSet<String> = bundle!!.getStringArrayList("currentFriendsInRoomList")!!.toHashSet()
        friendsViewModel.currentRoomFriendsSet.addAll(currentFriendsInRoomSet)
        searchFriendsListAdapter.ignoreIds(currentFriendsInRoomSet)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentInviteFriendsBinding.inflate(inflater)

        binding.inviteFriendsLayout.inviteFriendsList.adapter = checkedFriendsListAdapter
        binding.inviteFriendsLayout.searchFriendsList.adapter = searchFriendsListAdapter
        binding.inviteFriendsLayout.inviteFriendsList.visibility = View.GONE

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendsViewModel.friendCheckedLiveData.observe(viewLifecycleOwner) {
            if (it.isChecked) {
                checkedFriendsListAdapter.addItem(it.friendDTO)
            } else {
                checkedFriendsListAdapter.removeItem(it.friendDTO)
                searchFriendsListAdapter.uncheckItem(it.friendDTO)
            }
        }

        friendsViewModel.searchResultFriendsLiveData.observe(viewLifecycleOwner) {
            searchFriendsListAdapter.setList(it)
        }

        binding.inviteFriendsLayout.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.apply { friendsViewModel.getFriends(toString()) }
            }
        })
        friendsViewModel.getFriends("")
    }

}