package com.example.wapapp2.view.calculation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentNewCalcBinding
import com.example.wapapp2.view.friends.FriendsFragment
import com.example.wapapp2.view.main.MainHostFragment


class NewCalcFragment : Fragment() {
    private lateinit var binding: FragmentNewCalcBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewCalcBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener{
            parentFragmentManager.popBackStack()
        }

        childFragmentManager.beginTransaction().add(binding.fragmentContainerView.id, FriendsFragment(), FriendsFragment::class.java.name)
            .commitAllowingStateLoss()
    }
}