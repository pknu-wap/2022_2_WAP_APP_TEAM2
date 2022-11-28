package com.example.wapapp2.view.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.CalendarFragmentBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.calendar.dialog.CalendarDialogFragment
import com.example.wapapp2.view.calendar.dialog.ReceiptItemClickListener
import com.example.wapapp2.view.main.MainHostFragment
import com.example.wapapp2.viewmodel.MyCalendarViewModel
import org.joda.time.DateTime

class CalendarFragment : Fragment(), ReceiptItemClickListener {
    private lateinit var binding: CalendarFragmentBinding
    private var dstDate: DateTime = DateTime()

    private val myCalendarViewModel by activityViewModels<MyCalendarViewModel>()
    private var dialogFragment : DialogFragment? = null

    companion object{
        val TAG = "CalendarFragment"
    }

    private val dayItemOnClickListener: ListOnClickListener<String> = ListOnClickListener<String> { dayISO8601, _ ->
        dialogFragment = CalendarDialogFragment( this::OnReceiptClicked )
        dialogFragment!!.arguments = Bundle().apply {
            putString("selectedDayISO8601", dayISO8601)
        }
        dialogFragment!!.show(childFragmentManager, CalendarDialogFragment.TAG)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = CalendarFragmentBinding.inflate(layoutInflater)
        binding.calendarRv.layoutManager = object : GridLayoutManager(context,7) { override fun canScrollVertically() = false }
        myCalendarViewModel.myReceiptMapLivedata.observe(viewLifecycleOwner){
            updateCal(myCalendarViewModel.myReceiptMap!!)
        }

        binding.calendarBtnBack.setOnClickListener(View.OnClickListener {
            dstDate = dstDate.minusMonths(1)
            updateCal(myCalendarViewModel.myReceiptMap ?: hashMapOf())
        })
        binding.calendarBtnNext.setOnClickListener(View.OnClickListener {
            dstDate = dstDate.plusMonths(1)
            updateCal(myCalendarViewModel.myReceiptMap ?: hashMapOf())
        })


        return binding.root
    }

    fun updateCal(hashMap: HashMap<String, ArrayList<ReceiptDTO>>) {
        binding.calendarDate.text = dstDate.toString("yyyy년 MM월")
        binding.calendarRv.adapter = CalendarAdapter(dstDate.withDayOfMonth(1), hashMap!! ,dayItemOnClickListener)
    }

    override fun OnReceiptClicked(roomID: String?) {
        Log.d("roomId",roomID.toString())
        if(roomID != null){
            dialogFragment?.dismiss()

            val fragment = CalcMainFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager

            fragment.arguments = Bundle().apply {
                putString("roomId", roomID)
                // argument로 영수증 목록 열기
            }
            fragmentManager.beginTransaction()
                .hide(fragmentManager.findFragmentByTag(MainHostFragment.TAG) as Fragment)
                .addToBackStack(MainHostFragment.TAG)
                .add(R.id.fragment_container_view, fragment, CalcMainFragment.TAG)
                .addToBackStack(CalcMainFragment.TAG).commit()
        }
    }
}