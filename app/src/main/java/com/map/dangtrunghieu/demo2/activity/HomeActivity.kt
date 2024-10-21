package com.map.dangtrunghieu.demo2.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.adapter.DayViewContainer
import com.map.dangtrunghieu.demo2.adapter.TransactionAdapter
import com.map.dangtrunghieu.demo2.dao.TransactionDAO
import com.map.dangtrunghieu.demo2.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

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

        binding.rvTransaction.apply {
            adapter = transactionAdapter
            itemAnimator = DefaultItemAnimator()
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        binding.tvToday.text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()
            }
        }

        binding.btnCalendar.setOnClickListener {
            binding.calendarView.isVisible = true
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100) // Adjust as needed
        val endMonth = currentMonth.plusMonths(100) // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)
    }

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