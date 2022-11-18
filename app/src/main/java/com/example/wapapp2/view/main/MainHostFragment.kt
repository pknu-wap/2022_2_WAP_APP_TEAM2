package com.example.wapapp2.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.example.wapapp2.view.calendar.CalenderFragment
import com.example.wapapp2.view.calculation.GrouplistFragment
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentMainHostBinding
import com.example.wapapp2.view.friends.FriendsFragment
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel


class MainHostFragment : Fragment() {
    private var _binding: FragmentMainHostBinding? = null
    private val binding get() = _binding!!
    private val friendsViewModel by activityViewModels<FriendsViewModel>()

    companion object {
        const val TAG = "MainHostFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        friendsViewModel.loadMyFriends()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val transaction = childFragmentManager.beginTransaction()
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
                    R.id.friends -> {
                        newFragment = FriendsFragment()
                        true
                    }
                    else -> false
                }

                transaction.add(binding.fragmentContainerView.id, newFragment!!, tag).addToBackStack(tag)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}