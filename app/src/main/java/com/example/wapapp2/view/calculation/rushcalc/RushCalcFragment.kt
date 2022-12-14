package com.example.wapapp2.view.calculation.rushcalc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentRushCalcBinding
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.example.wapapp2.viewmodel.RushCalcViewModel
import com.example.wapapp2.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class RushCalcFragment : Fragment() {
    companion object {
        const val TAG = "RushCalcFragment"
    }

    private var _binding: FragmentRushCalcBinding? = null
    private val binding get() = _binding!!
    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({
        parentFragmentManager.findFragmentByTag(CalcMainFragment.TAG)!!
    })
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()
    private val rushCalcRoomViewModel by viewModels<RushCalcViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    private var participantsAdapter: ParticipantsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        participantsAdapter = ParticipantsAdapter(rushCalcRoomViewModel)


        rushCalcRoomViewModel.myUserName = myAccountViewModel.myProfileData.value!!.name
        rushCalcRoomViewModel.myUid = myAccountViewModel.myProfileData.value!!.id
        rushCalcRoomViewModel.calcRoomId = currentCalcRoomViewModel.roomId!!
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRushCalcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        userViewModel.users.observe(viewLifecycleOwner) {
            for (user in it) {
                participantsAdapter!!.participants.getValue(user.id).fcmToken = user.fcmToken
            }
            binding.participants.adapter = participantsAdapter
        }

        currentCalcRoomViewModel.participantMap.value!!.apply {
            val map = this.toMutableMap()
            val myUid = myAccountViewModel.myProfileData.value!!.id

            val idList = mutableListOf<String>()

            for (participantId in map.keys) {
                if (participantId == myUid) {
                    map.remove(myUid)
                    break
                } else {
                    idList.add(participantId)
                }
            }
            participantsAdapter!!.participants.putAll(map)
            userViewModel.getUsers(idList)
        }

        binding.rushCalcBtn.setOnClickListener {
            if (rushCalcRoomViewModel.selectedRecipientTokens.isEmpty()) {
                Toast.makeText(requireContext().applicationContext,
                        R.string.not_selected_recipients, Toast.LENGTH_SHORT).show()
            } else {
                MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.calc_rush)
                        .setMessage(R.string.msg_request_rush_calc)
                        .setNegativeButton(R.string.cancel) { dialog, which ->
                            dialog.dismiss()
                        }.setPositiveButton(R.string.check) { dialog, which ->
                            dialog.dismiss()
                            rushCalcRoomViewModel.sendRushCalcFcm()
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }.create().show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}