package com.example.wapapp2.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.wapapp2.view.calendar.CalenderFragment
import com.example.wapapp2.view.calculation.GrouplistFragment
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentMainHostBinding


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
            val transaction = childFragmentManager.beginTransaction()
            val bundle = Bundle()

            val currentFragment = childFragmentManager.primaryNavigationFragment

            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            val fragmentTag = item.title.toString()

            var newFragment: Fragment? = childFragmentManager.findFragmentByTag(fragmentTag)

            if (newFragment == null) {
                when (item.itemId) {
                    R.id.calendar -> {
                        newFragment = CalenderFragment()

                        true
                    }
                    R.id.calculation -> {
                        newFragment = GrouplistFragment()
                        true
                    }

                    else -> false
                }

                transaction.add(binding.fragmentContainerView.id, newFragment!!,
                        tag).addToBackStack(tag).setPrimaryNavigationFragment(newFragment)

            } else {
                transaction.show(newFragment)
            }

            transaction.setPrimaryNavigationFragment(newFragment).commitAllowingStateLoss()
            true
        }

        binding.bottomNavigationView.setOnItemReselectedListener {
            val currentSelectedItemId = binding.bottomNavigationView.selectedItemId
        }


        binding.bottomNavigationView.selectedItemId = R.id.calculation
    }
}