package com.example.wapapp2.view.checkreceipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentCheckReceiptBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.editreceipt.EditReceiptFragment
import com.example.wapapp2.viewmodel.ReceiptViewModel

class CheckReceiptFragment : Fragment() {

    private var _binding: FragmentCheckReceiptBinding? = null
    private val binding get() = _binding!!

    private var calcRoomId: String? = null
    private val receiptViewModel by viewModels<ReceiptViewModel>()

    private val receiptOnClickListener = ListOnClickListener<ReceiptDTO> { item, position ->
        val fragment = EditReceiptFragment()
        fragment.arguments = Bundle().apply {
            putParcelable("receiptDTO", item)
            putString("calcRoomId", calcRoomId)
        }
        val fragmentManager = parentFragmentManager

        fragmentManager
                .beginTransaction()
                .hide(this@CheckReceiptFragment)
                .add(R.id.fragment_container_view, fragment, "EditReceiptFragment")
                .addToBackStack("EditReceiptFragment")
                .commit()
    }
    private val adapter = CheckReceiptAdapter(onClickListener = receiptOnClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            calcRoomId = getString("calcRoomId")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckReceiptBinding.inflate(inflater)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rvEditreceipt.setHasFixedSize(true)
        binding.rvEditreceipt.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiptViewModel.receipts.observe(viewLifecycleOwner) {
            adapter.receipts = it
        }

        receiptViewModel.getReceipts(calcRoomId!!)
    }
}