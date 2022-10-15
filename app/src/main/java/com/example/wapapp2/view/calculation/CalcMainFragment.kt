package com.example.wapapp2.view.calculation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentCalcMainBinding
import com.example.wapapp2.view.chat.ChatFragment


class CalcMainFragment : Fragment() {
    private lateinit var binding: FragmentCalcMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCalcMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val recentTopFragment = RecentCalcTopFragment()
        val chatFragment = ChatFragment()

        val fragmentManager = childFragmentManager
        fragmentManager.beginTransaction().replace(binding.calculationSimpleInfo.id,
                recentTopFragment, RecentCalcTopFragment::class.simpleName)
                .replace(binding.chat.id, chatFragment, ChatFragment::class.simpleName).commitAllowingStateLoss()


    }
}