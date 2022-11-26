package com.example.wapapp2.view.calculation.calcroom

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.commons.classes.WrapContentLinearLayoutManager
import com.example.wapapp2.databinding.FragmentNewCalcRoomBinding
import com.example.wapapp2.view.friends.adapter.CheckedFriendsListAdapter
import com.example.wapapp2.view.friends.adapter.SearchFriendsListAdapter
import com.example.wapapp2.view.friends.interfaces.OnCheckedFriendListener
import com.example.wapapp2.view.friends.interfaces.OnRemovedFriendListener
import com.example.wapapp2.viewmodel.FriendsViewModel


class NewCalcRoomFragment : Fragment() {
    private var _binding: FragmentNewCalcRoomBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "NewCalcRoomFragment"
    }

    private val calcRoomViewModel: FriendsViewModel by activityViewModels()

    private val onCheckedFriendListener: OnCheckedFriendListener =
            OnCheckedFriendListener { isChecked, friendDTO -> calcRoomViewModel.checkedFriend(friendDTO, isChecked) }
    private val onRemovedFriendListener: OnRemovedFriendListener =
            OnRemovedFriendListener { friendDTO -> calcRoomViewModel.checkedFriend(friendDTO, false) }

    private val searchFriendsListAdapter: SearchFriendsListAdapter = SearchFriendsListAdapter(onCheckedFriendListener)
    private val checkedFriendsListAdapter: CheckedFriendsListAdapter = CheckedFriendsListAdapter(onRemovedFriendListener)

    private var listAdapterDataObserver: ListAdapterDataObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewCalcRoomBinding.inflate(inflater, container, false)
        binding.inviteFriendsLayout.searchFriendsList.layoutManager = WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager
                .VERTICAL, false)

        listAdapterDataObserver = ListAdapterDataObserver(binding.inviteFriendsLayout.searchFriendsList, binding.inviteFriendsLayout
                .searchFriendsList.layoutManager as LinearLayoutManager, searchFriendsListAdapter)
        searchFriendsListAdapter.registerAdapterDataObserver(listAdapterDataObserver!!)

        binding.inviteFriendsLayout.searchFriendsList.adapter = searchFriendsListAdapter
        binding.inviteFriendsLayout.inviteFriendsList.adapter = checkedFriendsListAdapter

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
                s?.apply { calcRoomViewModel.findFriend(toString()) }
            }
        })
        calcRoomViewModel.findFriend("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}