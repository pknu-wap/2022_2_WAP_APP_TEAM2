package com.example.wapapp2.view.editreceipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentEditReceiptBinding
import com.example.wapapp2.view.checkreceipt.CheckReceiptAdapter
import com.example.wapapp2.view.checkreceipt.ReceiptList

class EditReceiptFragment : Fragment() {

    private lateinit var binding: FragmentEditReceiptBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditReceiptBinding.inflate(inflater)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val itemList = arrayListOf(
            ItemList("배이컨토마토디럭스 세트", "8000원"),
            ItemList("더블 불고기 버거 세트", "6000원"),
            ItemList("1995버거 세트", "8000원"),
        )

        binding.rvEditreceipt.setHasFixedSize(true)
        binding.rvEditreceipt.adapter = EditReceiptAdapter(itemList)

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}