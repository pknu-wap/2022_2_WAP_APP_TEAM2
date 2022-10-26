package com.example.wapapp2.view.calculation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentManager
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentCalcMainBinding
import com.example.wapapp2.view.chat.ChatFragment


class CalcMainFragment : Fragment() {
    private lateinit var binding: FragmentCalcMainBinding
    private lateinit var bundle: Bundle

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            super.onFragmentCreated(fm, f, savedInstanceState)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)

        bundle = (arguments ?: savedInstanceState) as Bundle
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

        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle

        val fragmentManager = childFragmentManager
        fragmentManager.beginTransaction()
                .replace(binding.chat.id, chatFragment, ChatFragment::class.simpleName).commitAllowingStateLoss()

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

            }
        })

        binding.calculationSimpleInfo.expandBtn.setOnClickListener(object : View.OnClickListener {
            var expanded = true

            override fun onClick(v: View?) {
                expanded = !expanded
                binding.calculationSimpleInfo.expandBtn.setImageResource(if (expanded) R.drawable.ic_baseline_expand_less_24 else
                    R.drawable.ic_baseline_expand_more_24)
                binding.calculationSimpleInfo.recentCalculationView.visibility = if (expanded) View.VISIBLE else View.GONE
            }
        })

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu ->
                    binding.root.openDrawer(binding.sideNavigation)
                else -> {}
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(bundle)
    }
}