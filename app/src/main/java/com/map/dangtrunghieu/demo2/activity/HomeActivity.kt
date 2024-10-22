package com.map.dangtrunghieu.demo2.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.adapter.DayViewContainer
import com.map.dangtrunghieu.demo2.adapter.MonthViewContainer
import com.map.dangtrunghieu.demo2.adapter.TransactionAdapter
import com.map.dangtrunghieu.demo2.dao.TransactionDAO
import com.map.dangtrunghieu.demo2.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val transactionDAO = TransactionDAO(this)
    private val transactionAdapter by lazy { TransactionAdapter() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponent()
        setOnclickListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setOnclickListener() {
        binding.rvTransaction.apply {
            adapter = transactionAdapter
            itemAnimator = DefaultItemAnimator()
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        binding.tvToday.text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

//        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
//            // Called only when a new container is needed.
//            override fun create(view: View) = DayViewContainer(view)
//
//            // Called every time we need to reuse a container.
//            override fun bind(container: DayViewContainer, data: CalendarDay) {
//                container.textView.text = data.date.dayOfMonth.toString()
//            }
//        }

        binding.btnCalendar.setOnClickListener {
//            binding.calendarView.isVisible = true
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initComponent() {
//        initCalendar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
//    fun initCalendar() {
//
//        val currentMonth = YearMonth.now()
//        val startMonth = currentMonth.minusMonths(100) // Adjust as needed
//        val endMonth = currentMonth.plusMonths(100) // Adjust as needed
//        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
//        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
//        binding.calendarView.scrollToMonth(currentMonth)
//
//        binding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
//            override fun create(view: View) = MonthViewContainer(view)
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
//                // Remember that the header is reused so this will be called for each month.
//                // However, the first day of the week will not change so no need to bind
//                // the same view every time it is reused.
//                if (container.titlesContainer.tag == null) {
//                    container.titlesContainer.tag = data.yearMonth
//                    container.titlesContainer.children.map { it as TextView }
//                        .forEachIndexed { index, textView ->
//                            val dayOfWeek = data.weekDays.first()[index].date.dayOfWeek
//                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
//                            textView.text = title
//                            // In the code above, we use the same `daysOfWeek` list
//                            // that was created when we set up the calendar.
//                            // However, we can also get the `daysOfWeek` list from the month data:
//                            // val daysOfWeek = data.weekDays.first().map { it.date.dayOfWeek }
//                            // Alternatively, you can get the value for this specific index:
//                            // val dayOfWeek = data.weekDays.first()[index].date.dayOfWeek
//                        }
//                }
//            }
//        }
//
//        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
//            override fun create(view: View) = DayViewContainer(view)
//            override fun bind(container: DayViewContainer, data: CalendarDay) {
//                container.textView.text = data.date.dayOfMonth.toString()
//                if (data.position == DayPosition.MonthDate) {
//                    container.textView.setTextColor(Color.WHITE)
//                } else {
//                    container.textView.setTextColor(Color.GRAY)
//                }
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        val categoryList = transactionDAO.getAllTransactionByDay()
        transactionAdapter.submitList(categoryList)
        val sumIn = categoryList.filter { it.catInOut.inOut.type == 0 }.sumOf { it.amount }
        val sumOut = categoryList.filter { it.catInOut.inOut.type == 1 }.sumOf { it.amount }

        binding.tvInValue.text = sumIn.toString()
        binding.tvOutValue.text = sumOut.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}