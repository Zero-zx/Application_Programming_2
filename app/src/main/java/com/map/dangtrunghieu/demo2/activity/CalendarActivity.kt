package com.map.dangtrunghieu.demo2.activity

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.dao.TransactionDAO
import com.map.dangtrunghieu.demo2.databinding.ActivityCalendarBinding
import com.map.dangtrunghieu.demo2.databinding.CalendarDayBinding
import com.map.dangtrunghieu.demo2.databinding.CalendarHeaderBinding
import com.map.dangtrunghieu.demo2.databinding.TransactionItemBinding
import com.map.dangtrunghieu.demo2.model.Transaction
import com.map.dangtrunghieu.demo2.utils.displayText
import com.map.dangtrunghieu.demo2.utils.getColorCompat
import com.map.dangtrunghieu.demo2.utils.setTextColorRes
import com.map.dangtrunghieu.demo2.utils.showSimplifiedMoney
import com.map.dangtrunghieu.demo2.utils.toDate
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class CalendarActivity : AppCompatActivity() {
    private var _binding: ActivityCalendarBinding? = null
    private val binding get() = _binding!!
    private val transactionAdapter = TransactionAdapter()
    private var selectedDate: LocalDate? = null
    private val transactionDAO by lazy {
        TransactionDAO(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private lateinit var transactions: Map<LocalDate, List<Transaction>>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactions = transactionDAO.getAllTransaction().groupBy { it.date.toDate() }
        setOnClickListeners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setOnClickListeners() {
        binding.rcvTrans.apply {
            layoutManager = LinearLayoutManager(this@CalendarActivity, RecyclerView.VERTICAL, false)
            adapter = transactionAdapter
        }
        transactionAdapter.notifyDataSetChanged()

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)
        configureBinders(daysOfWeek)
        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarView.scrollToMonth(currentMonth)

        binding.calendarView.monthScrollListener = { month ->
            binding.exFiveMonthYearText.text = month.yearMonth.displayText()

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.calendarView.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        binding.exFiveNextMonthImage.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.exFivePreviousMonthImage.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAdapterForDate(date: LocalDate?) {
        transactionAdapter.transactions.clear()
        transactionAdapter.transactions.addAll(transactions[date].orEmpty())
        transactionAdapter.notifyDataSetChanged()
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        @RequiresApi(Build.VERSION_CODES.O)
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            val binding = this@CalendarActivity.binding
                            binding.calendarView.notifyDateChanged(day.date)
                            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
                            updateAdapterForDate(day.date)
                        }
                    }
                }
            }
        }
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun create(view: View) = DayViewContainer(view)

            @RequiresApi(Build.VERSION_CODES.O)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val context = container.binding.root.context
                val textView = container.binding.exFiveDayText
                val layout = container.binding.exFiveDayLayout
                textView.text = data.date.dayOfMonth.toString()

                val flightTopView = container.binding.tvIn
                val flightBottomView = container.binding.tvOut
                flightTopView.background = null
                flightBottomView.background = null

                if (data.position == DayPosition.MonthDate) {
                    textView.setTextColorRes(R.color.gray)
//                    layout.setBackgroundResource(if (selectedDate == data.date) R.drawable.bl else 0)

                    val transactionList = transactions[data.date]
                    if (transactionList != null) {
                        val inAmount = transactionList.filter { it.catInOut.inOut.type == 0 }
                            .sumOf { it.amount }
                        val outAmount = transactionList.filter { it.catInOut.inOut.type == 1 }
                            .sumOf { it.amount }
                        if (inAmount > 0) {
                            flightTopView.showSimplifiedMoney(inAmount)
                            flightTopView.setTextColor(context.getColorCompat(R.color.white))
                        } else {
                            flightTopView.text = ""
                        }
                        if (outAmount > 0) {
                            flightBottomView.showSimplifiedMoney(outAmount)
                            flightBottomView.setTextColor(context.getColorCompat(R.color.white))
                        } else {
                            flightBottomView.text = ""
                        }
                    } else {
                        textView.setTextColorRes(R.color.gray)
                        layout.background = null
                    }
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        val typeFace = Typeface.create("sans-serif-light", Typeface.NORMAL)
        binding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)

                @RequiresApi(Build.VERSION_CODES.O)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = true
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].displayText(uppercase = true)
                                tv.setTextColorRes(R.color.white)
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                                tv.typeface = typeFace
                            }
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


class TransactionAdapter :
    RecyclerView.Adapter<TransactionAdapter.Example5transactionListViewHolder>() {
    val transactions = mutableListOf<Transaction>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Example5transactionListViewHolder {
        return Example5transactionListViewHolder(
            TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(viewHolder: Example5transactionListViewHolder, position: Int) {
        viewHolder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    inner class Example5transactionListViewHolder(val binding: TransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {

            binding.tvTitle.text = transaction.name
            binding.tvAmountValue.text = transaction.amount.toString()
            binding.ivIcon.setImageResource(
                R.drawable.ic_work
            )
            if (transaction.catInOut.inOut.type == 0) {
                binding.tvAmountValue.setTextColor(binding.root.context.resources.getColor(R.color.green))
            } else {
                binding.tvAmountValue.setTextColor(binding.root.context.resources.getColor(R.color.red))
            }
        }
    }
}
