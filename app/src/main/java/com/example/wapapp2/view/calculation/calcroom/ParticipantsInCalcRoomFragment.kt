package com.example.wapapp2.view.calculation.calcroom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentParticipantsInCalcRoomBinding
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.view.calculation.calcroom.adapters.ParticipantsInCalcRoomAdapter
import com.example.wapapp2.view.friends.InviteFriendsFragment
import com.example.wapapp2.viewmodel.CalcRoomViewModel


class ParticipantsInCalcRoomFragment : Fragment() {
    private var _binding: FragmentParticipantsInCalcRoomBinding? = null
    private val binding get() = _binding!!
    private val calcRoomViewModel by viewModels<CalcRoomViewModel>({ requireParentFragment() })
    private val participantOnClickListener = ListOnClickListener<UserDTO> { item, pos ->

    }

    private val adapter = ParticipantsInCalcRoomAdapter(participantOnClickListener)
    var onNavDrawerListener: OnNavDrawerListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentParticipantsInCalcRoomBinding.inflate(inflater, container, false)

        binding.friends.setHasFixedSize(true)
        binding.friends.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calcRoomViewModel.participants.observe(viewLifecycleOwner) {
            adapter.participants = it
        }

        binding.addFriend.profileImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_group_add_24))
        binding.addFriend.friendName.text = "친구 초대"

        binding.addFriend.root.setOnClickListener {
            onNavDrawerListener?.closeDrawer()
            val inviteFriendsFragment = InviteFriendsFragment()
            inviteFriendsFragment.arguments = Bundle().apply {
                //현재 정산방 친구 목록 ID set생성
                val currentFriendsListInRoom = ArrayList<String>()
                val currentFriendDTOList = calcRoomViewModel.currentFriendsList

                for (dto in currentFriendDTOList) {
                    currentFriendsListInRoom.add(dto.friendUserId)
                }

                putStringArrayList("currentFriendsInRoomList", currentFriendsListInRoom)
            }
            val tag = "inviteFriends"
            val fragmentManager = requireParentFragment().parentFragmentManager

            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("CalcMain")!!).add(R.id.fragment_container_view,
                    inviteFriendsFragment, tag)
                    .addToBackStack(tag).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnNavDrawerListener {
        fun closeDrawer()
    }
}