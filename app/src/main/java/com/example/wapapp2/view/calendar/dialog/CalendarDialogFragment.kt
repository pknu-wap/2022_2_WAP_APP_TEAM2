package com.example.wapapp2.view.calendar.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.wapapp2.databinding.FragmentCalendarDialogBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.viewmodel.CalendarDialogViewModel
import com.example.wapapp2.viewmodel.MyCalendarViewModel
import org.joda.time.DateTime
import org.joda.time.Days
import kotlin.math.abs


class CalendarDialogFragment(private val receiptItemClickListener: ReceiptItemClickListener) : DialogFragment() {
    private lateinit var binding: FragmentCalendarDialogBinding
    private val calendarDialogViewModel by viewModels<CalendarDialogViewModel>()
    private val myCalendarViewModel by activityViewModels<MyCalendarViewModel>()

    lateinit var hashMap: HashMap<String, ArrayList<ReceiptDTO>>
    private val FIRST_VIEW_PAGER_POSITION = Int.MAX_VALUE / 2

    private lateinit var viewPagerAdapter: DialogViewPagerAdapter

    companion object {
        const val TAG = "calendarDialog"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hashMap = myCalendarViewModel.getTotalHashMap()
        calendarDialogViewModel.arguments = arguments ?: savedInstanceState
        calendarDialogViewModel.firstSelectedDay = calendarDialogViewModel.arguments!!.getString("selectedDayISO8601", "")
        val dstKey = DateTime.parse(calendarDialogViewModel.firstSelectedDay).toString("yyyyMMdd")
        viewPagerAdapter =
                DialogViewPagerAdapter(hashMap, calendarDialogViewModel.firstSelectedDay, requireContext(), receiptItemClickListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(calendarDialogViewModel.arguments)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCalendarDialogBinding.inflate(inflater)


        binding.viewPager.apply {
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics).toInt()

            offscreenPageLimit = 1
            getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(margin))
            compositePageTransformer.addTransformer(ViewPager2.PageTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.8f + r * 0.2f
            })

            setPageTransformer(compositePageTransformer)
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                var lastPosition = FIRST_VIEW_PAGER_POSITION

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.viewPager.adapter!!.notifyItemChanged(lastPosition)
                    lastPosition = position
                }
            })

            adapter = viewPagerAdapter
            setCurrentItem(FIRST_VIEW_PAGER_POSITION, false)
        }


        binding.goToTodayBtn.setOnClickListener {
            goToToday()
        }

        binding.goToFirstSelectedDayBtn.setOnClickListener {
            goToFirstSelectedDay()
        }

        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), com.example.wapapp2.R.style.DialogTransparent)
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    private fun goToToday() {
        val now = DateTime.now()
        val firstSelectedDay = DateTime.parse(calendarDialogViewModel.firstSelectedDay)
        val dayDifference = Days.daysBetween(firstSelectedDay.withTimeAtStartOfDay(), now.withTimeAtStartOfDay()).days

        refreshViews()
        if (binding.viewPager.currentItem != FIRST_VIEW_PAGER_POSITION + dayDifference) {
            binding.viewPager.setCurrentItem(FIRST_VIEW_PAGER_POSITION + dayDifference, true)
        }
    }

    private fun goToFirstSelectedDay() {
        refreshViews()
        if (binding.viewPager.currentItem != FIRST_VIEW_PAGER_POSITION) {
            binding.viewPager.setCurrentItem(FIRST_VIEW_PAGER_POSITION, true)
        }
    }

    private fun refreshViews() {
        val currentItem = binding.viewPager.currentItem
        binding.viewPager.adapter!!.notifyItemRangeChanged(currentItem - 3, 6)
    }
}