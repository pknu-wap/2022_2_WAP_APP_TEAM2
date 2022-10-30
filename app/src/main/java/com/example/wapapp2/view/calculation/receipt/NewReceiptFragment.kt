package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.wapapp2.databinding.FragmentNewReceiptBinding
import com.example.wapapp2.observer.MyLifeCycleObserver
import com.example.wapapp2.viewmodel.NewReceiptViewModel


open class NewReceiptFragment : Fragment() {
    private lateinit var binding: FragmentNewReceiptBinding
    private val newReceiptViewModel: NewReceiptViewModel by viewModels()
    private lateinit var adapter: NewReceiptViewPagerAdapter
    private lateinit var myObserver: DefaultLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = NewReceiptViewPagerAdapter(this)
        myObserver = MyLifeCycleObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(myObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewReceiptBinding.inflate(inflater)
        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.viewPager.also {

            it.adapter = adapter
            val indicator = binding.indicator
            it.adapter!!.registerAdapterDataObserver(indicator.adapterDataObserver)
            indicator.setViewPager(it)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addReceiptItem.setOnClickListener {
            adapter.addFragment()
            binding.viewPager.setCurrentItem(adapter.itemCount - 1, true)
            binding.receiptCount.text = adapter.itemCount.toString()
        }

        newReceiptViewModel.removeReceiptLiveData.observe(viewLifecycleOwner) {
            it?.apply {
                adapter.remove(it)
                binding.viewPager.setCurrentItem(adapter.itemCount - 1, true)
                binding.receiptCount.text = adapter.itemCount.toString()
            }
        }

        adapter.addFragment()
    }

}