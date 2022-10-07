package com.example.wapapp2.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentMainHostBinding
import com.google.android.material.navigation.NavigationBarView


class MainHostFragment : Fragment() {
    private lateinit var binding: FragmentMainHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainHostBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.calendar -> {

                    true
                }
                R.id.calculation -> {

                    true
                }
                R.id.friends -> {

                    true
                }
                else -> false
            }
        }

        binding.bottomNavigationView.setOnItemReselectedListener {
            val currentSelectedItemId = binding.bottomNavigationView.selectedItemId
        }

    }
}