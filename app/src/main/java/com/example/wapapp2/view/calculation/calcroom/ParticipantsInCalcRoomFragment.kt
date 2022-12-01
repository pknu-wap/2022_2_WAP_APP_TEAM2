package com.example.wapapp2.view.calculation.calcroom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentParticipantsInCalcRoomBinding
import com.example.wapapp2.model.CalcRoomParticipantDTO
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.calculation.calcroom.adapters.ParticipantsInCalcRoomAdapter
import com.example.wapapp2.view.friends.InviteFriendsFragment
import com.example.wapapp2.viewmodel.CalculationViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel
import com.example.wapapp2.viewmodel.MyCalcRoomViewModel


class ParticipantsInCalcRoomFragment : Fragment() {
    private var _binding: FragmentParticipantsInCalcRoomBinding? = null
    private val binding get() = _binding!!
    private val calcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment() })
    private val calculationViewModel by viewModels<CalculationViewModel>({
        parentFragmentManager.findFragmentByTag(CalcMainFragment.TAG)!!
    })

    private val participantOnClickListener = ListOnClickListener<CalcRoomParticipantDTO> { item, pos ->

    }

    companion object {
        const val TAG = "ParticipantsInCalcRoomFragment"
    }

    private val adapter = ParticipantsInCalcRoomAdapter(participantOnClickListener)
    var onNavDrawerListener: OnNavDrawerListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentParticipantsInCalcRoomBinding.inflate(inflater, container, false)

        binding.friends.setHasFixedSize(true)
        binding.friends.adapter = adapter

        binding.addFriend.profileImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_group_add_24))
        binding.addFriend.friendName.text = "친구 초대"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calcRoomViewModel.participantMap.observe(viewLifecycleOwner) {
            adapter.participants = it.values.toMutableList()
        }

        binding.addFriend.root.setOnClickListener {
            if (calcRoomViewModel.calcRoom.value!!.calculationStatus) {
                Toast.makeText(requireContext(), R.string.impossible_to_invite_friends_because_of_calculation, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onNavDrawerListener?.closeDrawer()
            val inviteFriendsFragment = InviteFriendsFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager

            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(CalcMainFragment.TAG)!!).add(R.id
                    .fragment_container_view, inviteFriendsFragment, InviteFriendsFragment.TAG).addToBackStack(InviteFriendsFragment.TAG).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun interface OnNavDrawerListener {
        fun closeDrawer()
    }
}