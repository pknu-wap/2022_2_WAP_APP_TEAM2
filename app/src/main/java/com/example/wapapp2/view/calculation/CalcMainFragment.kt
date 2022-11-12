package com.example.wapapp2.view.calculation

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout

import androidx.core.content.ContextCompat.getColor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.*
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.view.calculation.calcroom.ParticipantsInCalcRoomFragment
import com.example.wapapp2.view.calculation.interfaces.OnFixOngoingCallback
import com.example.wapapp2.view.calculation.interfaces.OnUpdateMoneyCallback
import com.example.wapapp2.view.calculation.interfaces.OnUpdateSummaryCallback
import com.example.wapapp2.view.calculation.receipt.DutchCheckFragment
import com.example.wapapp2.view.calculation.receipt.DutchPriceFragment
import com.example.wapapp2.view.chat.ChatFragment
import com.example.wapapp2.view.checkreceipt.ReceiptsFragment
import com.example.wapapp2.viewmodel.CalcRoomViewModel
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.example.wapapp2.viewmodel.ReceiptViewModel
import java.text.DecimalFormat


class CalcMainFragment : Fragment(), OnUpdateMoneyCallback, OnFixOngoingCallback, OnUpdateSummaryCallback,
        ParticipantsInCalcRoomFragment.OnNavDrawerListener {
    private lateinit var binding: FragmentCalcMainBinding
    private lateinit var bundle: Bundle

    private val calcRoomViewModel: CalcRoomViewModel by viewModels()
    private val receiptViewModel: ReceiptViewModel by viewModels()
    private val friendsViewModel by viewModels<FriendsViewModel>({ requireActivity() })

    /** summary of FixedPay **/
    private var paymoney = 0
    private var calcRoomId: String = DummyData.testCalcRoomId

    private var chatInputLayoutHeight = 0


    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            super.onFragmentCreated(fm, f, savedInstanceState)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)

        calcRoomViewModel.currentFriendsList.clear()
        calcRoomViewModel.currentFriendsList.addAll(DummyData.getFriendsInRoomList())

        bundle = (arguments ?: savedInstanceState) as Bundle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCalcMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        setSideMenu()

        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle

        chatFragment.setViewHeightCallback { height ->
            // 최근 정산 카드뷰를 펼쳤을때 폴더블 뷰의 하단 마진을 채팅 입력 레이아웃 높이로 변경
            chatInputLayoutHeight = height + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f,
                    resources.displayMetrics).toInt()
        }

        childFragmentManager.beginTransaction()
                .replace(binding.chat.id, chatFragment, ChatFragment::class.simpleName).commit()

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


    private fun updateFixedPay() {
        if (paymoney >= 0) {
            binding.calculationSimpleInfo.summary.text = "+ ${DecimalFormat("#,###").format(paymoney)}"
        } else {
            binding.calculationSimpleInfo.summary.text = paymoney.toString()

            binding.calculationSimpleInfo.summary.setTextColor(getColor(requireContext(), R.color.payMinus))

        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(bundle)
    }

    private fun setOngoingFolderView() {
        //정산 확정 전
        childFragmentManager.beginTransaction()
                .add(binding.calculationSimpleInfo.fragmentContainerView.id, DutchCheckFragment(this@CalcMainFragment::onFixOngoingReceipt, this@CalcMainFragment::updateSummaryUI), DutchCheckFragment::class.java.name)
                .commitAllowingStateLoss()


        binding.calculationSimpleInfo.expandBtn.setOnClickListener(object : View.OnClickListener {
            var expanded = true
            val collapsedMarginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f,
                    resources.displayMetrics)

            override fun onClick(v: View?) {
                expanded = !expanded
                binding.calculationSimpleInfo.expandBtn.setImageResource(if (expanded) R.drawable.ic_baseline_expand_less_24 else
                    R.drawable.ic_baseline_expand_more_24)
                binding.calculationSimpleInfo.fragmentContainerView.visibility = if (expanded) View.VISIBLE else View.GONE
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
                        updateSummaryUI()
                    }
                }
            })
        })


    }


    private fun setSideMenu() {
        binding.receiptsList.setOnClickListener {
            val fragment = ReceiptsFragment()
            fragment.arguments = Bundle().apply {
                putString("calcRoomId", calcRoomId)
            }
            val fragmentManager = parentFragmentManager
            fragmentManager
                    .beginTransaction()
                    .hide(this@CalcMainFragment)
                    .add(R.id.fragment_container_view, fragment, "CheckReceiptFragment")
                    .addToBackStack("CheckReceiptFragment")
                    .commit()
        }
        binding.exitRoom.setOnClickListener {

        }

        val participantsInCalcRoomFragment = ParticipantsInCalcRoomFragment()
        participantsInCalcRoomFragment.onNavDrawerListener = this
        childFragmentManager.beginTransaction().replace(binding.participantsFragmentContainer.id,
                participantsInCalcRoomFragment, "participants").commit()
    }


    fun interface ViewHeightCallback {
        fun height(height: Int)
    }

    override fun onUpdateMoney(money: Int) {
        paymoney += money
        updateFixedPay() //마지막에 최종 업데이트 되도록 수정 필요
    }

    override fun onFixOngoingReceipt() {
        childFragmentManager.beginTransaction()
                .replace(binding.calculationSimpleInfo.fragmentContainerView.id, DutchPriceFragment(this@CalcMainFragment::onUpdateMoney))
                .commitAllowingStateLoss()
    }

    override fun updateSummaryUI() {
        binding.calculationSimpleInfo.summary.text = DecimalFormat("#,###").format(receiptViewModel.getCurrentSummary())
    }

    override fun closeDrawer() {
        binding.root.closeDrawers()
    }

}