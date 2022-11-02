package com.example.wapapp2.view.checkreceipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wapapp2.databinding.FragmentCheckReceiptBinding
import com.example.wapapp2.databinding.FragmentEditCalcBinding

class CheckReceiptFragment : Fragment() {

    private lateinit var viewBinding: FragmentCheckReceiptBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCheckReceiptBinding.inflate(inflater)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}