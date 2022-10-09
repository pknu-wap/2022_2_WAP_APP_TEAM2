package com.example.wapapp2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.GroupFragmentBinding
import com.example.wapapp2.databinding.GroupItemBinding

class GrouplistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: GroupFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = GroupFragmentBinding.inflate(layoutInflater)
        val groupItem = arrayListOf<GroupItem>(
            GroupItem("2022-09-23", "OOO, OOO, OOO 외 1명", "정산완료") ,
            GroupItem("2022-09-27", "OOO, OOO, OOO 외 3명", "정산진행중..")
        )
        binding.groupRV.adapter = GroupAdapter( context , groupItem )
        return binding.root
        //return inflater.inflate(R.layout.group_fragment, container, false)
    }

    inner class GroupItem(val date : String, val names : String, val state : String)

    inner class GroupAdapter(private val context: Context?, val items: ArrayList<GroupItem>)
        : RecyclerView.Adapter<GroupAdapter.GroupVH>() {

        private lateinit var binding: GroupItemBinding

        inner class GroupVH(itemView: View?) : RecyclerView.ViewHolder(itemView!!){
            fun bind (groupItem: GroupItem){
                binding.groupItemDate.setText(groupItem.date)
                binding.groupItemNames.setText(groupItem.names)
                binding.groupItemState.setText(groupItem.state)
            }
        }


        override fun onBindViewHolder(holder: GroupVH, position: Int) {
            holder.bind(items.get(position))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupVH {
            binding = GroupItemBinding.inflate(LayoutInflater.from(context))
            return GroupVH(binding.root)
        }


    }
}