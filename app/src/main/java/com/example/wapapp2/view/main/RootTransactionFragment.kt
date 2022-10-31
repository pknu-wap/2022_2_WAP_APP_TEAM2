package com.example.wapapp2.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentLoginBinding
import com.example.wapapp2.databinding.FragmentRootTransactionBinding


class RootTransactionFragment : Fragment() {
    private lateinit var binding: FragmentRootTransactionBinding

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!childFragmentManager.popBackStackImmediate()) {
                if (!parentFragmentManager.popBackStackImmediate()) {
                    requireActivity().finish()
                }
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentRootTransactionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction().add(binding.fragmentContainerView.id, MainHostFragment(), MainHostFragment::class.java.name)
                .commitAllowingStateLoss()
    }
}