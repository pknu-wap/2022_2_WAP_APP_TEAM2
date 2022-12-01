package com.example.wapapp2.view.calculation.calcroom

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.databinding.FragmentNewCalcRoomBinding
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.friends.adapter.CheckedFriendsListAdapter
import com.example.wapapp2.view.friends.adapter.SearchFriendsListAdapter
import com.example.wapapp2.view.friends.interfaces.OnCheckedFriendListener
import com.example.wapapp2.view.friends.interfaces.OnRemovedFriendListener
import com.example.wapapp2.view.main.MainHostFragment
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.example.wapapp2.viewmodel.MyCalcRoomViewModel


class NewCalcRoomFragment : Fragment() {
    private lateinit var binding: FragmentNewCalcRoomBinding

    companion object {
        const val TAG = "NewCalcRoomFragment"
    }

    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()
    private val myCalcRoomViewModel by activityViewModels<MyCalcRoomViewModel>()
    private val friendsViewModel by viewModels<FriendsViewModel>()

    private val onCheckedFriendListener: OnCheckedFriendListener =
            OnCheckedFriendListener { isChecked, friendDTO -> friendsViewModel.checkedFriend(friendDTO, isChecked) }
    private val onRemovedFriendListener: OnRemovedFriendListener =
            OnRemovedFriendListener { friendDTO -> friendsViewModel.checkedFriend(friendDTO, false) }

    private val searchFriendsListAdapter: SearchFriendsListAdapter = SearchFriendsListAdapter(onCheckedFriendListener)
    private val checkedFriendsListAdapter: CheckedFriendsListAdapter = CheckedFriendsListAdapter(onRemovedFriendListener)

    private var listAdapterDataObserver: ListAdapterDataObserver? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentNewCalcRoomBinding.inflate(inflater)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.inviteFriendsLayout.inviteFriendsList.adapter = checkedFriendsListAdapter
        binding.inviteFriendsLayout.searchFriendsList.apply {
            adapter = searchFriendsListAdapter

            listAdapterDataObserver = ListAdapterDataObserver(
                this,
                this.layoutManager as LinearLayoutManager,
                this.adapter!!::getItemCount)
            listAdapterDataObserver!!.registerLoadingView(binding.inviteFriendsLayout.loadingView, getString(R.string.empty_friends_list))
            searchFriendsListAdapter!!.registerAdapterDataObserver(listAdapterDataObserver!!)
        }

        // 정산방 등록
        binding.inviteFriendsLayout.saveBtn.setOnClickListener {
            if (myAccountViewModel.myProfileData == null){
                Toast.makeText(context, "네트워크 연결을 확인하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.roomNameEdit.text!!.isEmpty()){
                Toast.makeText(context, "채팅방 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (checkedFriendsListAdapter.itemCount <= 0){
                Toast.makeText(context, "친구를 초대해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newCalcRoomDTO : CalcRoomDTO = CalcRoomDTO()
            newCalcRoomDTO.name = binding.roomNameEdit.text.toString()
            newCalcRoomDTO.creatorUserId = myAccountViewModel.myProfileData!!.value!!.id
            newCalcRoomDTO.participantIds = checkedFriendsListAdapter.getParticipantIDs(newCalcRoomDTO.creatorUserId)

            //backstack 막아야합니다.

            myCalcRoomViewModel.addNewCalcRoom(newCalcRoomDTO) {
                // newRoomAdded Callback -> 생성된 방 열기
                val fragment = CalcMainFragment()
                val fragmentManager = parentFragmentManager

                fragment.arguments = Bundle().apply {
                    putString("roomId", it.id)
                }

                fragmentManager.popBackStack()
                fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(MainHostFragment.TAG) as Fragment)
                    .add(R.id.fragment_container_view, fragment, CalcMainFragment.TAG)
                    .addToBackStack(CalcMainFragment.TAG).commitAllowingStateLoss()

            }
        }


        return binding.root
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
                s?.apply { friendsViewModel.findFriend(toString()) }
            }
        })
        friendsViewModel.findFriend("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapterDataObserver?.apply {
            searchFriendsListAdapter.unregisterAdapterDataObserver(this)
        }
    }
}