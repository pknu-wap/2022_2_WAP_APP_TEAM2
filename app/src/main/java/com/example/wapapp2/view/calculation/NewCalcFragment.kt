package com.example.wapapp2.view.calculation

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import com.example.wapapp2.R
import com.example.wapapp2.databinding.CalculationFriendLayoutInNewCalcBinding
import com.example.wapapp2.databinding.CalculationItemLayoutInNewCalcBinding
import com.example.wapapp2.databinding.FragmentNewCalcBinding
import com.example.wapapp2.model.CalculationItemDTO
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.observer.MyLifeCycleObserver
import com.example.wapapp2.view.friends.FriendsListFragment
import com.example.wapapp2.viewmodel.NewCalculationViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


open class NewCalcFragment : Fragment() {
    private lateinit var binding: FragmentNewCalcBinding
    private val newCalculationViewModel: NewCalculationViewModel by viewModels()
    private lateinit var myObserver: DefaultLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myObserver = MyLifeCycleObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(myObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewCalcBinding.inflate(inflater)
        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newCalculationViewModel.removeCalculationItemLiveData.observe(viewLifecycleOwner) {
            binding.menuList.removeViewAt(it)
            if (binding.toggles.checkedButtonId == binding.manualInput.id) {
                calcTotalPrice()
            }
        }

        binding.addCalculationItem.setOnClickListener {
            addCalculationItem()
        }

        newCalculationViewModel.friendsListLiveData.observe(viewLifecycleOwner) {
            addFriendsItem(it)
        }

        binding.toggles.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId) {
                binding.divideN.id -> {
                    // 정산 아이템 제거
                    val calculationItemCount = binding.menuList.childCount - 1
                    if (calculationItemCount > 0) {
                        for (i in calculationItemCount - 1..0) {
                            newCalculationViewModel.removeCalculationItem(i)
                        }
                    }
                    //총 금액 초기화
                    binding.totalMoneyEditText.text = "".toEditable()
                }
                binding.manualInput.id -> {
                    //총 금액 계산
                    calcTotalPrice()
                }
                else -> {

                }
            }

        }

        binding.receiptBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.add_receipt)
                    .setNegativeButton(R.string.exit) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.shoot_camera) { dialog, which ->
                        dialog.dismiss()

                    }
                    .setPositiveButton(R.string.pick_image) { dialog, which ->
                        dialog.dismiss()
                        //myObserver.pickImage()
                    }.create()

            dialog.show()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_friend -> {
                    val friendsListFragment = FriendsListFragment()
                    val tag = "friendsList"
                    parentFragmentManager.beginTransaction().hide(this@NewCalcFragment).add(R.id.fragment_container_view,
                            friendsListFragment, tag)
                            .addToBackStack(tag).commit()
                }
                else -> {}
            }
            true
        }

        binding.divideN.isSelected = true
    }

    private fun addCalculationItem() {
        val itemBinding = CalculationItemLayoutInNewCalcBinding.inflate(layoutInflater)
        itemBinding.root.tag = itemBinding

        val calculationItemDTO = CalculationItemDTO("", 0)
        val addedPosition = binding.menuList.childCount - 1

        itemBinding.calculationItemNameEditText.addTextChangedListener {
            if (!it.isNullOrEmpty())
                calculationItemDTO.itemName = it.toString()
        }
        itemBinding.calculationItemPriceEditText.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                calculationItemDTO.price = it.toString().toInt()
                calcTotalPrice()
            } else
                itemBinding.calculationItemPriceEditText.text = "0".toEditable()
        }

        itemBinding.removeBtn.setOnClickListener {
            newCalculationViewModel.removeCalculationItem(addedPosition)
        }

        binding.menuList.addView(itemBinding.root, addedPosition)
        newCalculationViewModel.addCalculationItem(calculationItemDTO)
    }

    private fun calcTotalPrice() {
        binding.totalMoneyEditText.text = newCalculationViewModel.calcTotalPrice().toEditable()
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun addFriendsItem(friendsList: ArrayList<FriendDTO>) {
        var itemBinding: CalculationFriendLayoutInNewCalcBinding? = null

        for (v in friendsList) {
            itemBinding = CalculationFriendLayoutInNewCalcBinding.inflate(layoutInflater)
            itemBinding.root.tag = itemBinding

            val addedPosition = binding.friendsList.childCount

            itemBinding.removeBtn.setOnClickListener {
                newCalculationViewModel.removeFriendDTOItem(addedPosition)
            }

            binding.friendsList.addView(itemBinding.root)
        }

    }

}