package com.example.wapapp2.view.calculation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.WrapContentLinearLayoutManager
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.commons.view.RecyclerViewItemDecoration
import com.example.wapapp2.databinding.GroupFragmentBinding
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.view.calculation.calcroom.NewCalcRoomFragment
import com.example.wapapp2.view.calculation.calcroom.adapters.GroupAdapter
import com.example.wapapp2.view.main.MainHostFragment
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.example.wapapp2.viewmodel.MyCalcRoomViewModel
import com.example.wapapp2.viewmodel.MyCalendarViewModel


class GrouplistFragment : Fragment() {
    private var _binding: GroupFragmentBinding? = null
    private val binding get() = _binding!!

    private var adapter: GroupAdapter? = null
    private val myCalcRoomViewModel by activityViewModels<MyCalcRoomViewModel>()
    private val myCalendarViewModel by activityViewModels<MyCalendarViewModel>()
    private val friendsViewModel by activityViewModels<FriendsViewModel>()

    var dataObserver: CalcRoomDataObserver? = null

    companion object {
        const val TAG = "GrouplistFragment"
    }

    /** Enter Group **/
    private val onGroupItemOnClickListener = ListOnClickListener<CalcRoomDTO> { item, pos ->
        val fragment = CalcMainFragment()
        val fragmentManager = requireParentFragment().parentFragmentManager

        fragment.arguments = Bundle().apply {
            putString("roomId", item.id)
        }

        fragmentManager.beginTransaction()
                .hide(fragmentManager.findFragmentByTag(MainHostFragment.TAG) as Fragment)
                .add(R.id.fragment_container_view, fragment, CalcMainFragment.TAG)
                .addToBackStack(CalcMainFragment.TAG).commit()
    }


    /** Add Room **/
    private val addOnClickedItemListener = View.OnClickListener {
        val fragment = NewCalcRoomFragment()
        val fragmentManager = requireParentFragment().parentFragmentManager

        fragmentManager.beginTransaction()
                .hide(fragmentManager.findFragmentByTag(MainHostFragment.TAG) as Fragment)
                .add(R.id.fragment_container_view, fragment, NewCalcRoomFragment.TAG)
                .addToBackStack(NewCalcRoomFragment.TAG).commit()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = GroupFragmentBinding.inflate(layoutInflater, container, false)
        binding.loadingView.setContentView(getString(R.string.empty_calc_rooms), binding.groupRV)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (binding.addBtn.height > 0) {
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    binding.groupRV.addItemDecoration(RecyclerViewItemDecoration(requireContext(), true,
                            binding.root.height - binding.addBtn.top - binding.addBtn.height / 2))
                }
            }
        })

        myCalcRoomViewModel.myCalcRoomIds.observe(viewLifecycleOwner) {
            adapter?.stopListening()
            if (it.isNotEmpty()) {
                if (adapter == null) {
                    adapter = GroupAdapter(myCalcRoomViewModel.getMyCalcRoomsOptions(), onGroupItemOnClickListener)
                    binding.groupRV.adapter = adapter
                    dataObserver = CalcRoomDataObserver(binding.groupRV, binding.groupRV.layoutManager as
                            WrapContentLinearLayoutManager, adapter!!)
                    dataObserver!!.registerLoadingView(binding.loadingView, getString(R.string.empty_calc_rooms))
                    adapter!!.registerAdapterDataObserver(dataObserver!!)
                } else {
                    adapter!!.updateOptions(myCalcRoomViewModel.getMyCalcRoomsOptions())
                }
                adapter!!.startListening()
            }
            myCalendarViewModel.loadCalendarReceipts(it)
        }
        binding.addBtn.setOnClickListener(addOnClickedItemListener)
        myCalcRoomViewModel.loadMyCalcRoomIds()

        if (friendsViewModel.friendsMap.value!!.isEmpty()) {
            friendsViewModel.loadMyFriends()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dataObserver?.apply {
            adapter!!.unregisterAdapterDataObserver(this)
        }

    }
}