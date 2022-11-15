package com.example.wapapp2.view.checkreceipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentCheckReceiptBinding
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.editreceipt.EditReceiptFragment
import com.example.wapapp2.viewmodel.ReceiptViewModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter

class ReceiptsFragment : Fragment() {
    private var _binding: FragmentCheckReceiptBinding? = null
    private val binding get() = _binding!!

    private var calcRoomId: String? = null
    private val receiptViewModel by viewModels<ReceiptViewModel>({ requireParentFragment() })

    private val receiptOnClickListener = ListOnClickListener<ReceiptDTO> { item, position ->
        val fragment = EditReceiptFragment()
        fragment.arguments = Bundle().apply {
            putParcelable("receiptDTO", item)
            putString("calcRoomId", calcRoomId)
        }

        parentFragmentManager
                .beginTransaction()
                .hide(this@ReceiptsFragment)
                .add(R.id.fragment_container_view, fragment, EditReceiptFragment.TAG)
                .addToBackStack(EditReceiptFragment.TAG)
                .commit()
    }
    private lateinit var adapter: ReceiptsAdapter

    private val dataObserver = object : RecyclerView.AdapterDataObserver() {
        val msg = getString(R.string.empty_receipts)

        override fun onChanged() {
            super.onChanged()
            onChangedItemCount()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            onChangedItemCount()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            onChangedItemCount()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            onChangedItemCount()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            onChangedItemCount()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            onChangedItemCount()
        }

        private fun onChangedItemCount() {
            if (adapter.itemCount > 0)
                binding.loadingView.onFailed(msg)
            else
                binding.loadingView.onSuccessful()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            calcRoomId = getString("calcRoomId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckReceiptBinding.inflate(inflater, container, false)
        binding.loadingView.setContentView(binding.rvEditreceipt)
        binding.loadingView.onSuccessful()

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = ReceiptsAdapter(receiptOnClickListener, receiptViewModel.getReceiptsRecyclerOptions(calcRoomId!!))
        adapter.registerAdapterDataObserver(dataObserver)

        binding.rvEditreceipt.setHasFixedSize(true)
        binding.rvEditreceipt.adapter = adapter
        adapter.startListening()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("calcRoomId", calcRoomId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.unregisterAdapterDataObserver(dataObserver)
        adapter.stopListening()
    }


}