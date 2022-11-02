package com.example.wapapp2.view.calculation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentEditCalcBinding
import com.example.wapapp2.view.login.ProfileAdapter


class EditCalcFragment : Fragment() {
    private lateinit var binding: FragmentEditCalcBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentEditCalcBinding.inflate(inflater)

        val itemList = arrayListOf(
            EditReceipt("햄버거", 6000),
            EditReceipt("콜라", 2000),
            EditReceipt("감자", 1000)
        )

        binding.rvEditreceipt.adapter = EditReceiptAdapter(itemList)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}