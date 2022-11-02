package com.example.wapapp2.view.calculation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.databinding.*
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.model.FixedPayDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.view.chat.ChatFragment
import com.example.wapapp2.view.friends.InviteFriendsFragment
import com.example.wapapp2.view.login.Profiles
import com.example.wapapp2.viewmodel.CalcRoomViewModel
import org.joda.time.DateTime


class CalcMainFragment : Fragment() {
    private lateinit var binding: FragmentCalcMainBinding
    private lateinit var bundle: Bundle
    private val calcRoomViewModel: CalcRoomViewModel by viewModels()

    private var summary = 0
    private var paymoney = 0

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            super.onFragmentCreated(fm, f, savedInstanceState)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)

        bundle = (arguments ?: savedInstanceState) as Bundle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCalcMainBinding.inflate(inflater)
        setInputListener()
        binding.calculationSimpleInfo.btnCalcAdd.setOnClickListener(View.OnClickListener {
            TODO("정산추가화면 구현 필요")
        })

        //toggle로 바꿀 예정
        binding.calculationSimpleInfo.btnCalcDone.setOnClickListener(View.OnClickListener {

            val dummyData = DummyData.getFixedDTOs()

            (binding.calculationSimpleInfo.viewReceipts.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 100
            binding.calculationSimpleInfo.viewReceipts.adapter = FixedPayAdapter(context, dummyData)


            binding.calculationSimpleInfo.btnCalcAdd.text = "정산 수정"
            binding.calculationSimpleInfo.btnCalcAdd.setOnClickListener(View.OnClickListener {
                TODO("정산수정버튼 구현 필요")

            })

            binding.calculationSimpleInfo.btnCalcDone.text = "정산 완료"
            binding.calculationSimpleInfo.btnCalcDone.setOnClickListener(View.OnClickListener {
                TODO("정산완료버튼 구현 필요")
            })
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setSideMenu()


        val dummyReceipts = DummyData.getReceipts()

        binding.calculationSimpleInfo.viewReceipts.adapter = ReceiptAdapter(context, dummyReceipts)

        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle

        val fragmentManager = childFragmentManager
        fragmentManager.beginTransaction()
                .replace(binding.chat.id, chatFragment, ChatFragment::class.simpleName).commitAllowingStateLoss()

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

            }
        })


        binding.calculationSimpleInfo.expandBtn.setOnClickListener(object : View.OnClickListener {
            var expanded = true

            override fun onClick(v: View?) {
                expanded = !expanded
                binding.calculationSimpleInfo.expandBtn.setImageResource(if (expanded) R.drawable.ic_baseline_expand_less_24 else
                    R.drawable.ic_baseline_expand_more_24)
                binding.calculationSimpleInfo.foldableView.visibility = if (expanded) View.VISIBLE else View.GONE
                binding.calculationSimpleInfo.checklistReceipts.layoutParams.height =
                        if (expanded) RelativeLayout.LayoutParams.MATCH_PARENT else RelativeLayout.LayoutParams.WRAP_CONTENT
            }
        })

        //default를 false로 수정할 필요.
        binding.calculationSimpleInfo.expandBtn.post(Runnable {
            binding.calculationSimpleInfo.expandBtn.callOnClick()
        })
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

    /** setListener For Input Box **/
    private fun setInputListener() {
        binding.clearBtn.setOnClickListener {
            binding.inputText.text = null
        }

        binding.sendBtn.setOnClickListener {
            if (binding.inputText.text.isNotEmpty()) {
                // 전송
            } else {
                Toast.makeText(requireContext(), "메시지를 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** summary 화면에 표시 **/
    private fun updateSummary() {
        binding.calculationSimpleInfo.summary.text = summary.toString() + "원"
    }

    private fun updateFixedPay() {
        if (paymoney >= 0) {
            binding.calculationSimpleInfo.summary.text = "+" + paymoney.toString()
            binding.calculationSimpleInfo.summary.setTextColor(getColor(requireContext(), R.color.payPlus))
        } else {
            binding.calculationSimpleInfo.summary.text = paymoney.toString()
            binding.calculationSimpleInfo.summary.setTextColor(getColor(requireContext(), R.color.payMinus))

        }
    }


    /** 영수증 Adapter **/
    private inner class ReceiptAdapter(private val context: Context?, private val receipts: ArrayList<ReceiptDTO>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        inner class ReceiptVM(val binding: ViewReceiptItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(receipt: ReceiptDTO) {
                binding.description.text = "[ " + receipt.title + " ] - 김진우"
                binding.recentCalcItem.adapter = ReceiptItemAdapter(context, receipt.getProducts())
                binding.dateTime.text = DateTime.parse(receipt.date).toString("yyyy-MM-dd")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ReceiptVM(ViewReceiptItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return (holder as ReceiptVM).bind(receipts[position])
        }

        override fun getItemCount(): Int {
            return receipts.size
        }

    }

    /** 영수증 세부 항목 Adapter **/
    private inner class ReceiptItemAdapter(private val context: Context?, private val items: ArrayList<ReceiptProductDTO>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        inner class ReceiptMenuVH(val binding: ViewRecentCalcItemBinding) : RecyclerView.ViewHolder(binding.root) {


            fun bind(item: ReceiptProductDTO) {
                var myMoney = calcMyMoney(item)
                binding.receiptMenu.text = item.itemName
                binding.receiptTotalMoney.text = item.price.toString()
                binding.receiptMyMoney.text = myMoney.toString()
                binding.receiptPersonCount.text = item.personCount.toString() + "/3"
                binding.recentCalcCkbox.isChecked = true

                summary += myMoney

                updateSummary()


                binding.recentCalcCkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        item.personCount++;
                        myMoney = calcMyMoney(item)
                        summary += myMoney

                        binding.receiptMyMoney.text = myMoney.toString()
                        binding.receiptPersonCount.text = item.personCount.toString() + "/3"
                        updateSummary()

                    } else {
                        myMoney = calcMyMoney(item)
                        summary -= myMoney
                        item.personCount--;

                        binding.receiptMyMoney.text = "0"
                        binding.receiptPersonCount.text = item.personCount.toString() + "/3"
                        updateSummary()
                    }

                }
            }

            fun calcMyMoney(item: ReceiptProductDTO): Int {
                return try {
                    item.price / item.personCount
                } catch (e: ArithmeticException) {
                    0
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ReceiptMenuVH(ViewRecentCalcItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return (holder as ReceiptMenuVH).bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }


    /** 확정 정산 금액 **/
    private inner class FixedPayAdapter(val context: Context?, val items: ArrayList<FixedPayDTO>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class FixedPayVH(val binding: ViewDutchItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: FixedPayDTO) {
                binding.name.text = item.id
                binding.pay.text = item.pay.toString()
                if (item.pay >= 0) {
                    binding.pay.text = "+" + binding.pay.text
                    binding.pay.setTextColor(getColor(requireContext(), R.color.payPlus))
                } else binding.pay.setTextColor(getColor(requireContext(), R.color.payMinus))


                paymoney += item.pay
                updateFixedPay()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return FixedPayVH(ViewDutchItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as FixedPayVH).bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }

    private inner class friendsAdapter(val context: Context?, val items: ArrayList<Profiles>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class friendsVH(val binding: ChatFriendsItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: Profiles) {
                binding.profileImg.setImageDrawable(getDrawable(requireContext(), item.gender))
                binding.friendName.text = item.name
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return friendsVH(ChatFriendsItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as friendsVH).bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(bundle)
    }

    private fun setSideMenu() {
        binding.receiptsList.setOnClickListener(View.OnClickListener { })
        binding.exitRoom.setOnClickListener(View.OnClickListener { })
        binding.addFriend.profileImg.setImageDrawable(getDrawable(requireContext(), R.drawable.ic_baseline_group_add_24))
        binding.addFriend.friendName.text = "친구 초대"
        binding.addFriend.friendName.isClickable = true

        binding.addFriend.friendName.setOnClickListener {
            val inviteFragment = InviteFriendsFragment()
            inviteFragment.arguments = Bundle().apply {
                //현재 정산방 친구 목록 ID set생성
                val currentFriendsListInRoom = ArrayList<String>()
                val currentFriendDTOList = calcRoomViewModel.currentFriendsList

                for (dto in currentFriendDTOList) {
                    currentFriendsListInRoom.add(dto.uid)
                }

                putStringArrayList("currentFriendsInRoomList", currentFriendsListInRoom)
            }
            val tag = "inviteFriends"
            val fragmentManager = requireParentFragment().parentFragmentManager
            fragmentManager.beginTransaction().hide(this@CalcMainFragment)
                    .add(R.id.fragment_container_view, inviteFragment, tag)
                    .addToBackStack(tag).commit()

        }
        val dummyFriends = DummyData.getProfiles()
    }

}