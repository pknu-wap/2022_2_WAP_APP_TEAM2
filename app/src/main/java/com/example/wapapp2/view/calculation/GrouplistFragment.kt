package com.example.wapapp2.view.calculation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.databinding.GroupFragmentBinding
import com.example.wapapp2.databinding.GroupItemBinding
import com.example.wapapp2.databinding.ListAddViewBinding
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.dummy.TestLogics
import com.example.wapapp2.model.GroupItemDTO
import com.example.wapapp2.view.calculation.calcroom.NewCalcRoomFragment
import com.example.wapapp2.view.calculation.receipt.NewReceiptFragment
import com.example.wapapp2.view.main.MainHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.joda.time.DateTime


class GrouplistFragment : Fragment() {
    private lateinit var binding: GroupFragmentBinding

    private lateinit var adapter: GroupAdapter

    /** Enter Group **/
    private val onClickedItemListener = object : OnClickedItemListener {
        override fun onClickedItem(position: Int) {
            val fragment = CalcMainFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager

            fragment.arguments = Bundle().apply {
                val dummyData = DummyData.getRoom()
                putString("roomId", dummyData.id)
            }
            val tag = "CalcMain"

            fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(MainHostFragment::class.java.name) as
                            Fragment)
                    .add(R.id.fragment_container_view, fragment, tag)
                    .addToBackStack(tag).commit()
        }

    }


    /** Add Group **/
    private val addOnClickedItemListener = View.OnClickListener {
        MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.add)
                .setNeutralButton(R.string.new_calculation) { dialog, which ->
                    //정산 추가
                    val fragment = NewReceiptFragment()
                    val fragmentManager = requireParentFragment().parentFragmentManager
                    dialog.dismiss()
                    fragmentManager.beginTransaction()
                            .hide(fragmentManager.findFragmentByTag(MainHostFragment::class.java.name) as
                                    Fragment)
                            .add(R.id.fragment_container_view, fragment, NewReceiptFragment::class.java.name)
                            .addToBackStack(NewReceiptFragment::class.java.name).commit()
                }.setPositiveButton(R.string.new_calc_room) { dialog, which ->
                    //정산방 추가
                    val fragment = NewCalcRoomFragment()
                    val fragmentManager = requireParentFragment().parentFragmentManager
                    dialog.dismiss()
                    fragmentManager.beginTransaction()
                            .hide(fragmentManager.findFragmentByTag(MainHostFragment::class.java.name) as
                                    Fragment)
                            .add(R.id.fragment_container_view, fragment, NewCalcRoomFragment::class.java.name)
                            .addToBackStack(NewCalcRoomFragment::class.java.name).commit()
                }.create().show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = GroupFragmentBinding.inflate(layoutInflater)
        val groupItem = DummyData.getGroupList()
        adapter = GroupAdapter(context, groupItem)
        binding.groupRV.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.groupRV.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        //binding.groupRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onStart() {
        super.onStart()
    }

    private enum class ItemViewType {
        ADD, GROUP_ITEM
    }

    private inner class GroupItem(val date: String, val names: String, val state: String)

    private inner class GroupAdapter(private val context: Context?, private val items: ArrayList<GroupItemDTO>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class GroupVH(val binding: GroupItemBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(groupItem: GroupItemDTO) {
                var nameString = ""
                var count = 1;
                for (name in groupItem.members) {
                    if (count > 3) {
                        nameString += " 외 " + (groupItem.members.size - 3) + "명"
                        break
                    } else {
                        nameString += name
                        if (count != groupItem.members.size && count != 3)
                            nameString += ", "
                    }
                    count++
                }

                binding.groupItemNames.text = nameString
                binding.groupItemState.text = groupItem.state
                binding.groupItemDate.text = DateTime.parse(groupItem.date).toString("yyyy-MM-dd")

                binding.root.setOnClickListener {
                    onClickedItemListener.onClickedItem(adapterPosition)
                }
            }

        }

        inner class AddVH(val binding: ListAddViewBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind() {
                binding.groupBtnAdd.setOnClickListener {
                    addOnClickedItemListener.onClick(binding.root)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return if (position == itemCount - 1) (holder as AddVH).bind() else (holder as GroupVH).bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size + 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == ItemViewType.GROUP_ITEM.ordinal) GroupVH(GroupItemBinding.inflate(LayoutInflater.from(context)))
            else AddVH(ListAddViewBinding.inflate(LayoutInflater.from(context)))
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == itemCount - 1) ItemViewType.ADD.ordinal else ItemViewType.GROUP_ITEM.ordinal
        }

    }

    private interface OnClickedItemListener {
        fun onClickedItem(position: Int)
    }
}