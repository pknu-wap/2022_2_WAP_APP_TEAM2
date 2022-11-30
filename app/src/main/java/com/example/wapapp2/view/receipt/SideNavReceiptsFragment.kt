package com.example.wapapp2.view.receipt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.commons.classes.WrapContentLinearLayoutManager
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentSideNavReceiptsBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel
import com.example.wapapp2.viewmodel.ReceiptViewModel


class SideNavReceiptsFragment : Fragment() {
    private var _binding: FragmentSideNavReceiptsBinding? = null
    private val binding get() = _binding!!
    private var adapter: SideNavSimpleReceiptsAdapter? = null

    companion object {
        const val TAG = "SideNavReceiptsFragment"
    }

    private val receiptViewModel by viewModels<ReceiptViewModel>()
    private val calcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment() })
    private var listAdapterDataObserver: ListAdapterDataObserver? = null

    private val receiptOnClickListener = ListOnClickListener<ReceiptDTO> { item, _ ->
        val receiptInfoFragment = ReceiptInfoFragment.newInstance(calcRoomViewModel.roomId!!, item)

        val fragmentManager = requireParentFragment().parentFragmentManager
        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(CalcMainFragment.TAG)!!)
                .add(R.id.fragment_container_view, receiptInfoFragment, ReceiptInfoFragment.TAG)
                .addToBackStack(ReceiptInfoFragment.TAG)
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = SideNavSimpleReceiptsAdapter(receiptOnClickListener, receiptViewModel.getReceiptsRecyclerOptions(calcRoomViewModel
                .roomId!!))
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSideNavReceiptsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingView.setContentView(getString(R.string.empty_receipts), binding.receiptList)

        listAdapterDataObserver =
                ListAdapterDataObserver(binding.receiptList, binding.receiptList.layoutManager as WrapContentLinearLayoutManager, adapter!!)
        listAdapterDataObserver!!.registerLoadingView(binding.loadingView, getString(R.string.empty_receipts))
        adapter!!.registerAdapterDataObserver(listAdapterDataObserver!!)
        binding.receiptList.adapter = adapter
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
}