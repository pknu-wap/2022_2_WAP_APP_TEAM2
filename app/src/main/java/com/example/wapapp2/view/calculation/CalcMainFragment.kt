package com.example.wapapp2.view.calculation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentCalcMainBinding
import com.example.wapapp2.databinding.ViewRecentCalcItemBinding
import com.example.wapapp2.model.CalcReceiptData
import com.example.wapapp2.view.chat.ChatFragment


class CalcMainFragment : Fragment() {
    private lateinit var binding: FragmentCalcMainBinding
    private lateinit var bundle: Bundle
    private var summary = 0

    fun updateSummary(){
        binding.calculationSimpleInfo.summary.text = summary.toString() + "원"
    }

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

        val dummyReceipt = ArrayList<CalcReceiptData>()
        dummyReceipt.add(CalcReceiptData("돼지고기",36000,12000,3))
        dummyReceipt.add(CalcReceiptData("된장찌개",6000,6000,1))

        binding.calculationSimpleInfo.recentCalcItem.adapter = ReceiptItemAdapter( context ,dummyReceipt)
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
                R.id.menu ->{
                    binding.root.openDrawer(binding.sideNavigation)
                }

                else -> {}
            }
            true
        }
    }

    private inner class ReceiptItemAdapter(private val context : Context? ,private val items : ArrayList<CalcReceiptData>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        inner class ReceiptVH(val binding : ViewRecentCalcItemBinding) : RecyclerView.ViewHolder(binding.root){

            fun bind(item : CalcReceiptData){
                try{item.myMoney = item.totalMoney / item.personCount}catch (e : ArithmeticException){ item.myMoney =0}
                binding.receiptMenu.text = item.menu
                binding.receiptTotalMoney.text = item.totalMoney.toString()
                binding.receiptMyMoney.text = item.myMoney.toString()
                binding.receiptPersonCount.text = item.personCount.toString() + "/3"
                binding.recentCalcCkbox.isChecked = true

                summary += item.myMoney


                binding.recentCalcCkbox.setOnCheckedChangeListener{ _, isChecked ->
                    if(isChecked){
                        summary -= item.myMoney
                        item.personCount++; item.myMoney = item.totalMoney / item.personCount
                        summary += item.myMoney


                        binding.receiptMyMoney.text = item.myMoney.toString()
                        binding.receiptPersonCount.text = item.personCount.toString() + "/3"
                        updateSummary()

                    }
                    else{
                        item.personCount--;
                        summary -= item.myMoney
                        try{item.myMoney = item.totalMoney / item.personCount}catch (e : ArithmeticException){ item.myMoney =0}
                        summary += item.myMoney

                        binding.receiptMyMoney.text = item.myMoney.toString()
                        binding.receiptPersonCount.text = item.personCount.toString() + "/3"
                        updateSummary()
                    }

                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ReceiptVH(ViewRecentCalcItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return (holder as ReceiptVH).bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(bundle)
    }
}