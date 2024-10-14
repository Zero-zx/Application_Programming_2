package com.map.dangtrunghieu.demo2.activity

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.adapter.CustomSpinnerAdapter
import com.map.dangtrunghieu.demo2.dao.CategoryDAO
import com.map.dangtrunghieu.demo2.dao.TransactionDAO
import com.map.dangtrunghieu.demo2.databinding.ActivityAddBinding
import com.map.dangtrunghieu.demo2.model.Category
import com.map.dangtrunghieu.demo2.utils.DateFormatter
import java.time.LocalDate

class AddActivity : AppCompatActivity() {
    private var _binding: ActivityAddBinding? = null
    private val binding get() = _binding!!
    private val transactionDAO = TransactionDAO(this)
    private val categoryDAO = CategoryDAO(this)
    private val categoryAdapter by lazy {
        CustomSpinnerAdapter(
            this,
            categoryDAO,
            categoryDAO.getCategoryByInOut(0).toMutableList()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpComponent()
        setOnClickListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpComponent() {
        binding.spCategory.adapter = categoryAdapter

        binding.etDay.text = LocalDate.now().format(DateFormatter.formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setOnClickListener() {
        binding.etDay.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                binding.etDay.text = selectedDate.format(DateFormatter.formatter)
            }
        }

        binding.btnAdd.setOnClickListener {
            if (binding.spCategory.selectedItem == null) {
                Toast.makeText(this, "You must select category first!", Toast.LENGTH_SHORT).show()
            } else if (binding.etAmount.text.isBlank()) {
                Toast.makeText(this, "You must input amount first!", Toast.LENGTH_SHORT).show()
            } else if (binding.etDay.text.isBlank()) {
                Toast.makeText(this, "You must input date first!", Toast.LENGTH_SHORT).show()
            } else {
                if (transactionDAO.addTransaction(
                        name = binding.etName.text.toString(),
                        amount = binding.etAmount.text.toString().toDouble(),
                        date = DateFormatter.reformatter(binding.etDay.text.toString()),
                        note = binding.etNote.text.toString(),
                        cat = binding.spCategory.selectedItem as Category,
                        inOut = if (binding.rbOut.isChecked) 2 else 1
                    )
                ) {
                    Toast.makeText(this, "Add transaction successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Add transaction failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.rbIn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                categoryAdapter.submitList(categoryDAO.getCategoryByInOut(0))
            } else {
                categoryAdapter.submitList(categoryDAO.getCategoryByInOut(1))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog(onDateSelected: (LocalDate) -> Unit) {
        // Get the current date
        val currentDate = LocalDate.now()
        val year = currentDate.year
        val month = currentDate.monthValue - 1 // DatePicker uses 0-indexed months
        val day = currentDate.dayOfMonth

        // Show DatePickerDialog
        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Convert selected date to LocalDate
            val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(selectedDate)
        }, year, month, day).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}