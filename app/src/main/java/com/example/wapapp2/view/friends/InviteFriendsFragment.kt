package com.example.wapapp2.view.friends

import android.os.Bundle
import android.os.Messenger
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DelayTextWatcher
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.databinding.FragmentInviteFriendsBinding
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.friends.adapter.CheckedFriendsListAdapter
import com.example.wapapp2.view.friends.adapter.SearchFriendsListAdapter
import com.example.wapapp2.view.friends.interfaces.OnCheckedFriendListener
import com.example.wapapp2.view.friends.interfaces.OnRemovedFriendListener
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class InviteFriendsFragment : Fragment() {
    private var _binding: FragmentInviteFriendsBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "InviteFriendsFragment"
    }

    private val calcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({
        parentFragmentManager.findFragmentByTag(CalcMainFragment.TAG)!!
    })
    private val friendsViewModel: FriendsViewModel by activityViewModels()

    private var listAdapterDataObserver: ListAdapterDataObserver? = null
    private val onCheckedFriendListener: OnCheckedFriendListener =
            OnCheckedFriendListener { isChecked, friendDTO -> friendsViewModel.checkedFriend(friendDTO, isChecked) }
    private val onRemovedFriendListener: OnRemovedFriendListener =
            OnRemovedFriendListener { friendDTO -> friendsViewModel.checkedFriend(friendDTO, false) }

    private val searchFriendsListAdapter: SearchFriendsListAdapter = SearchFriendsListAdapter(onCheckedFriendListener)
    private val checkedFriendsListAdapter: CheckedFriendsListAdapter = CheckedFriendsListAdapter(onRemovedFriendListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        friendsViewModel.participantsInCalcRoom.addAll(calcRoomViewModel.participantMap.value!!.values.toMutableList())
        searchFriendsListAdapter.ignoreIds(calcRoomViewModel.participantMap.value!!.keys.toMutableSet())
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentInviteFriendsBinding.inflate(inflater, container, false)

        binding.inviteFriendsLayout.loadingView.setContentView(binding.inviteFriendsLayout.searchFriendsList)

        binding.inviteFriendsLayout.inviteFriendsList.adapter = checkedFriendsListAdapter
        binding.inviteFriendsLayout.searchFriendsList.adapter = searchFriendsListAdapter

        //처음 화면 실행 시에는 선택된 친구 목록 레이아웃 숨김
        binding.inviteFriendsLayout.inviteFriendsList.visibility = View.GONE

        listAdapterDataObserver = ListAdapterDataObserver(binding.inviteFriendsLayout.searchFriendsList, binding.inviteFriendsLayout
                .searchFriendsList.layoutManager as LinearLayoutManager, searchFriendsListAdapter)
        listAdapterDataObserver!!.registerLoadingView(binding.inviteFriendsLayout.loadingView, getString(R.string.no_search_results_found))
        searchFriendsListAdapter.registerAdapterDataObserver(listAdapterDataObserver!!)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendsViewModel.friendCheckedLiveData.observe(viewLifecycleOwner) {
            if (it.isChecked) {
                checkedFriendsListAdapter.addItem(it.friendDTO)
                binding.inviteFriendsLayout.inviteFriendsList.visibility = View.VISIBLE
            } else {
                checkedFriendsListAdapter.removeItem(it.friendDTO)
                searchFriendsListAdapter.uncheckItem(it.friendDTO)

                if (checkedFriendsListAdapter.itemCount == 0)
                    binding.inviteFriendsLayout.inviteFriendsList.visibility = View.GONE
            }
        }

        friendsViewModel.searchResultFriendsLiveData.observe(viewLifecycleOwner) {
            searchFriendsListAdapter.setList(it)
        }

        binding.inviteFriendsLayout.searchBar.addTextChangedListener(object : DelayTextWatcher() {
            override fun onFinalText(text: String) {
                friendsViewModel.findFriend(text)
            }
        })

        binding.inviteFriendsLayout.saveBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.invite_friends)
                    .setMessage(R.string.msg_invite_new_friends)
                    .setPositiveButton(R.string.invite) { dialog, which ->
                        calcRoomViewModel.inviteFriends(checkedFriendsListAdapter.friends.toMutableList(), calcRoomViewModel.roomId!!)
                        dialog.dismiss()
                    }.setNegativeButton(R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }.create().show()
        }

        friendsViewModel.findFriend("")
    }

    override fun onDestroy() {
        friendsViewModel.searchResultFriendsLiveData.value?.clear()
        searchFriendsListAdapter.unregisterAdapterDataObserver(listAdapterDataObserver!!)
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}