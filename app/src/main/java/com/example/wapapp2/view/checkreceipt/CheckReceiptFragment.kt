package com.example.wapapp2.view.checkreceipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentCheckReceiptBinding
import com.example.wapapp2.view.editreceipt.EditReceiptFragment

class CheckReceiptFragment : Fragment() {

    private lateinit var binding: FragmentCheckReceiptBinding

    private val receiptOnClickListener = ListOnClickListener<ReceiptList> { item, position ->
        val fragment = EditReceiptFragment()
        val fragmentManager = parentFragmentManager

        fragmentManager
            .beginTransaction()
            .hide(this@CheckReceiptFragment)
            .add(R.id.fragment_container_view, fragment, "EditReceiptFragment")
            .addToBackStack("EditReceiptFragment")
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckReceiptBinding.inflate(inflater)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val receiptList = arrayListOf(
            ReceiptList("22,000원", "멕도날드", "2022.11.02", R.drawable.receipt),
            ReceiptList("12,000원", "스타벅스", "2022.10.31", R.drawable.receipt),
            ReceiptList("58,000원", "VIPS", "2022.10.28", R.drawable.receipt)
        )

        binding.rvEditreceipt.setHasFixedSize(true)
        binding.rvEditreceipt.adapter = CheckReceiptAdapter(receiptList, receiptOnClickListener)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}