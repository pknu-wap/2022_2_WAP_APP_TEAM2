package com.example.wapapp2.view.calculation


import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DataTypeConverter
import com.example.wapapp2.databinding.FragmentCalcMainBinding
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.view.calculation.calcroom.ParticipantsInCalcRoomFragment
import com.example.wapapp2.view.calculation.receipt.DutchCheckFragment
import com.example.wapapp2.view.calculation.rushcalc.RushCalcFragment
import com.example.wapapp2.view.chat.ChatFragment
import com.example.wapapp2.view.checkreceipt.ReceiptsFragment
import com.example.wapapp2.viewmodel.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class CalcMainFragment : Fragment(), ParticipantsInCalcRoomFragment.OnNavDrawerListener {
    private var _binding: FragmentCalcMainBinding? = null
    private val binding get() = _binding!!
    private var roomId: String? = null

    companion object {
        const val TAG = "CalcMainFragment"
    }

    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val calculationViewModel by viewModels<CalculationViewModel>()
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()

    private var chatInputLayoutHeight = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            roomId = getString("roomId")!!
            currentCalcRoomViewModel.loadCalcRoomData(roomId!!)
            calculationViewModel.calcRoomId = roomId!!
        }

        calculationViewModel.myUserName = myAccountViewModel.myProfileData.value!!.name
        calculationViewModel.myUid = myAccountViewModel.myProfileData.value!!.id

        currentCalcRoomViewModel.myFriendMap.putAll(FriendsViewModel.MY_FRIEND_MAP.toMutableMap())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCalcMainBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSideMenu()

        calculationViewModel.mySettlementAmount.observe(viewLifecycleOwner) { transferMoney ->
            updateMySettlementAmount(transferMoney)
        }

        currentCalcRoomViewModel.calcRoom.observe(viewLifecycleOwner) {
            binding.topAppBar.title = it.name
            binding.roomTitle.text = it.name
        }

        calculationViewModel.receiptMap.observe(viewLifecycleOwner) {
            if (it.isEmpty) {
                binding.calculationSimpleInfo.title.text = getString(R.string.empty_ongoing_receipts)
                binding.calculationSimpleInfo.summary.visibility = View.GONE
            } else {
                binding.calculationSimpleInfo.title.text = getString(R.string.my_settlement_amount)
                binding.calculationSimpleInfo.summary.visibility = View.VISIBLE
                updateMySettlementAmount(calculationViewModel.mySettlementAmount.value!!)
            }
        }

        val chatFragment = ChatFragment()
        chatFragment.setViewHeightCallback { height ->
            // 최근 정산 카드뷰를 펼쳤을때 폴더블 뷰의 하단 마진을 채팅 입력 레이아웃 높이로 변경
            chatInputLayoutHeight = height + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f,
                    resources.displayMetrics).toInt()
        }

        childFragmentManager.beginTransaction()
                .replace(binding.chat.id, chatFragment, ChatFragment.TAG).commit()

        setOngoingFolderView()

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu -> {
                    binding.root.openDrawer(binding.sideNavigation)
                }
                else -> {}
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("roomId", roomId)
    }

    private fun updateMySettlementAmount(transferMoney: Int) {
        if (transferMoney >= 0) {
            val value = "+ ${DataTypeConverter.toKRW(transferMoney)}"
            binding.calculationSimpleInfo.summary.text = value
        } else {
            binding.calculationSimpleInfo.summary.text = DataTypeConverter.toKRW(transferMoney)
            binding.calculationSimpleInfo.summary.setTextColor(getColor(requireContext().applicationContext, R.color.payMinus))
        }
    }

    private fun setOngoingFolderView() {
        //정산 확정 전
        childFragmentManager.beginTransaction()
                .replace(binding.calculationSimpleInfo.calculationFragmentContainerView.id, DutchCheckFragment(), DutchCheckFragment.TAG)
                .commit()

        binding.calculationSimpleInfo.expandBtn.setOnClickListener(object : View.OnClickListener {
            var expanded = true
            val collapsedMarginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f,
                    resources.displayMetrics)

            override fun onClick(v: View?) {
                expanded = !expanded
                binding.calculationSimpleInfo.expandBtn.setImageResource(if (expanded) R.drawable.ic_baseline_expand_less_24 else
                    R.drawable.ic_baseline_expand_more_24)
                binding.calculationSimpleInfo.calculationFragmentContainerView.visibility = if (expanded) View.VISIBLE else View.GONE
                binding.calculationSimpleInfo.checklistReceipts.layoutParams.height =
                        if (expanded) LinearLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.WRAP_CONTENT

                // 최근 정산 카드뷰를 펼쳤을때 폴더블 뷰의 하단 마진을 채팅 입력 레이아웃 높이로 변경
                val cardViewLayoutParams = binding.calculationSimpleInfo.root.layoutParams as FrameLayout.LayoutParams
                cardViewLayoutParams.bottomMargin = if (expanded) chatInputLayoutHeight else collapsedMarginBottom.toInt()

                binding.calculationSimpleInfo.root.layoutParams = cardViewLayoutParams
            }
        })

        //default를 false로 수정할 필요.
        binding.calculationSimpleInfo.expandBtn.post(Runnable {
            binding.calculationSimpleInfo.expandBtn.callOnClick()

            binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.calculationSimpleInfo.root.height > 0) {
                        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        //채팅 프래그먼트의 상단 마진 값을 최근정산 접었을때 높이 + 8dp로 설정
                        val chatFragmentContainerLayoutParams = binding.chat.layoutParams as FrameLayout.LayoutParams
                        chatFragmentContainerLayoutParams.topMargin = binding.calculationSimpleInfo.root.height
                        binding.chat.layoutParams = chatFragmentContainerLayoutParams
                    }
                }
            })
        })


    }


    private fun setSideMenu() {
        binding.receiptsList.setOnClickListener {
            val fragment = ReceiptsFragment()
            fragment.arguments = Bundle().apply {
                putString("roomId", roomId)
            }
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().hide(this@CalcMainFragment)
                    .add(R.id.fragment_container_view, fragment, "CheckReceiptFragment")
                    .addToBackStack("CheckReceiptFragment")
                    .commit()
        }

        binding.rushCalcBtn.setOnClickListener {
            if (calculationViewModel.receiptMap.value!!.isEmpty) {
                Toast.makeText(requireContext().applicationContext, R.string.no_available_rush_calc, Toast.LENGTH_SHORT).show()
            } else {
                val rushCalcFragment = RushCalcFragment()
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction().hide(this@CalcMainFragment)
                        .add(R.id.fragment_container_view, rushCalcFragment, RushCalcFragment.TAG)
                        .addToBackStack(RushCalcFragment.TAG)
                        .commit()
            }
        }

        binding.exitRoom.setOnClickListener {
            //방 나가기
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.exit_from_room)
                    .setMessage(R.string.msg_exit_from_calc_room)
                    .setPositiveButton(R.string.exit) { dialog, which ->
                        dialog.dismiss()
                        if (currentCalcRoomViewModel.calcRoom.value!!.calculationStatus)
                            Toast.makeText(context, "진행중인 정산이 존재합니다!", Toast.LENGTH_SHORT).show()
                        else {
                            currentCalcRoomViewModel.exitFromRoom(currentCalcRoomViewModel.roomId!!)
                            val myProfile = myAccountViewModel.myProfileData.value!!
                            val noticeChatDTO = ChatDTO(myProfile.name, null, "", myProfile.id, true)
                            chatViewModel.sendMsg(noticeChatDTO){}
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }.setNegativeButton(R.string.close) { dialog, which ->
                        dialog.dismiss()
                    }.create().show()
        }

        val participantsInCalcRoomFragment = ParticipantsInCalcRoomFragment()
        participantsInCalcRoomFragment.onNavDrawerListener = this
        childFragmentManager.beginTransaction().replace(binding.participantsFragmentContainer.id,
                participantsInCalcRoomFragment, ParticipantsInCalcRoomFragment.TAG).commit()
    }


    fun interface ViewHeightCallback {
        fun height(height: Int)
    }

    override fun closeDrawer() {
        binding.root.closeDrawers()
    }

}