package com.example.wapapp2.view.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.CalenderFragmentBinding
import com.example.wapapp2.databinding.CalenderItemBinding
import org.joda.time.DateTime

class CalenderFragment : Fragment() {
    private lateinit var dstDate: DateTime
    private lateinit var binding: CalenderFragmentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fun updateCal() {
            binding.calenderDate.text = dstDate.toString("yyyy년 MM월")
            binding.calenderRV.adapter = CalenderAdapter(dstDate.year, dstDate.monthOfYear)
        }

        dstDate = org.joda.time.DateTime()
        binding = CalenderFragmentBinding.inflate(layoutInflater)

        updateCal()
        binding.calenderBtnBack.setOnClickListener(View.OnClickListener {
            dstDate = dstDate.minusMonths(1)
            updateCal()
        })
        binding.calenderBtnNext.setOnClickListener(View.OnClickListener {
            dstDate = dstDate.plusMonths(1)
            updateCal()
        })


        return binding.root
    }


    private inner class CalenderAdapter(year: Int, month: Int) : RecyclerView.Adapter<CalenderAdapter.ViewHolder>() {
        private val row = 7;
        private val col = 5
        private var dayStart: Int
        private var maxDay: Int

        private lateinit var item_binding: CalenderItemBinding

        init {
            val dstDate = DateTime().withDate(year, month, 1)
            dayStart = -(dstDate.dayOfWeek - 1)
            maxDay = dstDate.toLocalDate().dayOfMonth().withMaximumValue().dayOfMonth

        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(pos: Int) {
                val dayCount = dayStart + pos
                if (dayCount in 1..maxDay)
                    item_binding.calenderDay.text = dayCount.toString()
                else
                    item_binding.calenderDay.text = ""
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            item_binding = CalenderItemBinding.inflate(LayoutInflater.from(context))
            return ViewHolder(item_binding.root)
        }


        override fun getItemCount(): Int {
            return row * col
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position)
        }


    }

}