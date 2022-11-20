package com.example.wapapp2.view.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.CalendarFragmentBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.calendar.dialog.CalendarDialogFragment
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.example.wapapp2.viewmodel.MyCalendarViewModel
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime

class CalendarFragment : Fragment() {
    private var dstDate: DateTime = DateTime()
    private lateinit var binding: CalendarFragmentBinding

    private var calendarAdapter: CalendarAdapter? = null

    private val friendsViewModel by activityViewModels<FriendsViewModel>()
    private val calendarViewModel by activityViewModels<MyCalendarViewModel>()

    companion object{
        val TAG = "CalendarFragment"
    }

    private val dayItemOnClickListener: ListOnClickListener<String> = ListOnClickListener<String> { dayISO8601, pos ->
        val dialogFragment = CalendarDialogFragment()
        dialogFragment.arguments = Bundle().apply {
            putString("selectedDayISO8601", dayISO8601)
        }
        dialogFragment.show(childFragmentManager, CalendarDialogFragment.TAG)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = CalendarFragmentBinding.inflate(layoutInflater)

        updateCal()

        calendarViewModel.myReceiptMap.observe(viewLifecycleOwner){
            Log.d("@@","UPDATED")
            updateCal()
        }



        binding.calendarBtnBack.setOnClickListener(View.OnClickListener {
            dstDate = dstDate.minusMonths(1)
            updateCal()
        })
        binding.calendarBtnNext.setOnClickListener(View.OnClickListener {
            dstDate = dstDate.plusMonths(1)
            updateCal()
        })


        return binding.root
    }


    fun updateCal() {
        val hashMap = calendarViewModel.myReceiptMap.value ?: hashMapOf()
        Log.d("update HashMap", hashMap.toString())
        binding.calendarDate.text = dstDate.toString("yyyy년 MM월")
        binding.calendarRv.adapter = CalendarAdapter(dstDate.withDayOfMonth(1), hashMap!! ,dayItemOnClickListener,)
    }
}