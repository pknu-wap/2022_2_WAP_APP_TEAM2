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

    companion object {
        const val TAG = "ReceiptsFragment"
    }

    private var calcRoomId: String? = null
    private val receiptViewModel by viewModels<ReceiptViewModel>()

    private val receiptOnClickListener = ListOnClickListener<ReceiptDTO> { item, _position ->
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
        adapter.stopListening()
    }


}