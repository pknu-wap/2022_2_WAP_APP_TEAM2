package com.example.wapapp2.view.calculation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentCalcMainBinding
import com.example.wapapp2.databinding.ViewReceiptItemBinding
import com.example.wapapp2.databinding.ViewRecentCalcItemBinding
import com.example.wapapp2.model.CalcReceiptMenuData
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.view.chat.ChatFragment


class CalcMainFragment : Fragment() {
    private lateinit var binding: FragmentCalcMainBinding
    private lateinit var bundle: Bundle

    var summary = 0

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


        binding.clearBtn.setOnClickListener {
            binding.inputText.text = null
        }

        binding.sendBtn.setOnClickListener {
            if (binding.inputText.text.isNotEmpty()) {
                // 전송
            } else {
                Toast.makeText(requireContext(), "메시지를 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
        //binding.calculationSimpleInfo.recentCalcItem.adapter = ReceiptItemAdapter( context ,dummyReceipt)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val dummyReceipts = ArrayList<ReceiptDTO>()

        val dummyReceipt1 = ReceiptDTO("점심계산")
        dummyReceipt1.addProduct(ReceiptProductDTO("돼지고기",36000,3))
        dummyReceipt1.addProduct(ReceiptProductDTO("된장찌개",6000,1))
        val dummyReceipt2 = ReceiptDTO("저녁계산")
        dummyReceipt2.addProduct(ReceiptProductDTO("숙소",100000,3))
        dummyReceipt2.addProduct(ReceiptProductDTO("치킨",25000,2))

        dummyReceipts.add(dummyReceipt1); dummyReceipts.add(dummyReceipt2)

        binding.calculationSimpleInfo.viewReceipts.adapter = ReceiptAdapter(context, dummyReceipts )

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
                binding.calculationSimpleInfo.foldableView.visibility = if (expanded) View.VISIBLE else View.GONE
                binding.calculationSimpleInfo.checklistReceipts.layoutParams.height =
                    if (expanded) RelativeLayout.LayoutParams.MATCH_PARENT  else RelativeLayout.LayoutParams.WRAP_CONTENT
            }
        })

        //default를 false로 수정할 필요.
        binding.calculationSimpleInfo.expandBtn.callOnClick()

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


    fun updateSummary(){
        binding.calculationSimpleInfo.summary.text = summary.toString() + "원"
    }

    private inner class ReceiptAdapter(private val context: Context?, private val receipts : ArrayList<ReceiptDTO>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


        inner class ReceiptVM(val binding : ViewReceiptItemBinding) : RecyclerView.ViewHolder(binding.root){
            fun bind(receipt : ReceiptDTO){
                binding.description.text = receipt.title
                binding.recentCalcItem.adapter = ReceiptItemAdapter(context, receipt.getProducts())
                binding.dateTime.text = receipt.date
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ReceiptVM(ViewReceiptItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return (holder as ReceiptVM).bind(receipts[position])
        }

        override fun getItemCount(): Int {
            return receipts.size
        }

    }

    private inner class ReceiptItemAdapter(private val context : Context? ,private val items : ArrayList<ReceiptProductDTO>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


        inner class ReceiptMenuVH(val binding : ViewRecentCalcItemBinding) : RecyclerView.ViewHolder(binding.root){


            fun bind(item : ReceiptProductDTO){
                var myMoney = calcMyMoney(item)
                binding.receiptMenu.text = item.itemName
                binding.receiptTotalMoney.text = item.price.toString()
                binding.receiptMyMoney.text = myMoney.toString()
                binding.receiptPersonCount.text = item.personCount.toString() + "/3"
                binding.recentCalcCkbox.isChecked = true

                summary += myMoney

                updateSummary()


                binding.recentCalcCkbox.setOnCheckedChangeListener{ _, isChecked ->
                    if(isChecked){
                        item.personCount++;
                        myMoney = calcMyMoney(item)
                        summary += myMoney

                        binding.receiptMyMoney.text = myMoney.toString()
                        binding.receiptPersonCount.text = item.personCount.toString() + "/3"
                        updateSummary()

                    }
                    else{
                        myMoney = calcMyMoney(item)
                        summary -= myMoney
                        item.personCount--;

                        binding.receiptMyMoney.text = "0"
                        binding.receiptPersonCount.text = item.personCount.toString() + "/3"
                        updateSummary()
                    }

                }
            }
            fun calcMyMoney(item : ReceiptProductDTO) :Int{
                return try{item.price / item.personCount}catch(e : ArithmeticException){ 0 }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ReceiptMenuVH(ViewRecentCalcItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return (holder as ReceiptMenuVH).bind(items[position])
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