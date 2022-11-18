package com.example.wapapp2.view.checkreceipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentCheckReceiptBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.editreceipt.EditReceiptFragment
import com.example.wapapp2.viewmodel.ReceiptViewModel

class ReceiptsFragment : Fragment() {
    private var _binding: FragmentCheckReceiptBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "ReceiptsFragment"
    }

    private var roomId: String? = null
    private val receiptViewModel by viewModels<ReceiptViewModel>()
    private var dataObserver: ListAdapterDataObserver? = null

    private val receiptOnClickListener = ListOnClickListener<ReceiptDTO> { item, _position ->
        val fragment = EditReceiptFragment()
        fragment.arguments = Bundle().apply {
            putParcelable("receiptDTO", item)
            putString("roomId", roomId)
        }

        parentFragmentManager
                .beginTransaction()
                .hide(this@ReceiptsFragment)
                .add(R.id.fragment_container_view, fragment, EditReceiptFragment.TAG)
                .addToBackStack(EditReceiptFragment.TAG)
                .commit()
    }
    private lateinit var adapter: ReceiptsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            roomId = getString("roomId")
        }
        roomId?.run {
            savedInstanceState?.getString("roomId")
        }

        receiptViewModel.roomId = roomId
        adapter = ReceiptsAdapter(receiptOnClickListener, receiptViewModel.getReceiptsRecyclerOptions(receiptViewModel.roomId!!))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckReceiptBinding.inflate(inflater, container, false)
        binding.loadingView.setContentView(binding.rvEditreceipt)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        dataObserver = ListAdapterDataObserver(binding.rvEditreceipt, binding.rvEditreceipt.layoutManager as
                LinearLayoutManager, adapter)
        dataObserver!!.registerLoadingView(binding.loadingView, getString(R.string.empty_receipts))
        adapter.registerAdapterDataObserver(dataObserver!!)

        binding.rvEditreceipt.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("roomId", roomId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dataObserver?.apply {
            adapter.unregisterAdapterDataObserver(this)
        }
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            adapter.stopListening()
        } else {
            adapter.startListening()
        }
    }


}