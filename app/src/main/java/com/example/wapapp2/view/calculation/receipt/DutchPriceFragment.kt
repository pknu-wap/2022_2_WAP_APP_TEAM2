package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wapapp2.databinding.DutchCheckFragmentBinding

class DutchPriceFragment : Fragment() {
    lateinit var binding : DutchCheckFragmentBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DutchCheckFragmentBinding.inflate(inflater)
        binding.btnCalcAdd

        return binding.root
    }


}