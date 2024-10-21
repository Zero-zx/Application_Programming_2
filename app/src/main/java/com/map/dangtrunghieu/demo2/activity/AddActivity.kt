package com.map.dangtrunghieu.demo2.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.adapter.CategoryTreeAdapter
import com.map.dangtrunghieu.demo2.dao.CatInOutDAO
import com.map.dangtrunghieu.demo2.dao.CategoryDAO
import com.map.dangtrunghieu.demo2.dao.TransactionDAO
import com.map.dangtrunghieu.demo2.databinding.ActivityAddBinding
import com.map.dangtrunghieu.demo2.model.Category
import com.map.dangtrunghieu.demo2.model.CategoryNode
import com.map.dangtrunghieu.demo2.utils.DateFormatter
import java.time.LocalDate

class AddActivity : AppCompatActivity() {
    private var _binding: ActivityAddBinding? = null
    private val binding get() = _binding!!
    private val transactionDAO = TransactionDAO(this)
    private val categoryDAO = CategoryDAO(this)
    private val catInOutDAO = CatInOutDAO(this)
    private lateinit var categoryAdapter: CategoryTreeAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val category = data?.getSerializableExtra("result", Category::class.java)
                category?.let {
                    val categoryTree = buildCategoryTree(
                        categoryDAO.getCategoryByInOut(
                            if (binding.rbIn.isChecked) 0 else 1
                        )
                    )
                    val flattenedTree = flattenCategoryTree(categoryTree)
                    flattenedTree.first { node -> node.category.id == category.id }.category

                    categoryAdapter = CategoryTreeAdapter(this, flattenedTree)
                    binding.spCategory.adapter = categoryAdapter
                    val pos = flattenedTree.indexOfFirst { node -> node.category.id == category.id }
                    binding.spCategory.setSelection(pos)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpComponent()
        setOnClickListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpComponent() {
        val categoryTree = buildCategoryTree(categoryDAO.getCategoryByInOut(0))
        val flattenedTree = flattenCategoryTree(categoryTree)
        categoryAdapter = CategoryTreeAdapter(this, flattenedTree)
        binding.spCategory.adapter = categoryAdapter
        binding.etDay.text = LocalDate.now().format(DateFormatter.formatter)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                        catInOut = catInOutDAO.insertCatInOut(
                            (binding.spCategory.selectedItem as CategoryNode).category,
                            if (binding.rbIn.isChecked) 0 else 1
                        )
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
                val categoryTree = buildCategoryTree(categoryDAO.getCategoryByInOut(0))
                val flattenedTree = flattenCategoryTree(categoryTree)
                categoryAdapter = CategoryTreeAdapter(this, flattenedTree)
                binding.spCategory.adapter = categoryAdapter
            } else {
                val categoryTree = buildCategoryTree(categoryDAO.getCategoryByInOut(1))
                val flattenedTree = flattenCategoryTree(categoryTree)
                categoryAdapter = CategoryTreeAdapter(this, flattenedTree)
                binding.spCategory.adapter = categoryAdapter
            }
        }

        binding.btnAddCategory.setOnClickListener {
            val intent = Intent(this, AddCategoryActivity::class.java)
            startForResult.launch(intent)

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

    private fun buildCategoryTree(categories: List<Category>): List<CategoryNode> {
        val categoryMap = categories.associateBy { it.id }
        val rootCategories = categories.filter { it.parent == null }

        fun buildNode(category: Category, level: Int): CategoryNode {
            val children = categories.filter { it.parent?.id == category.id }
            return CategoryNode(
                category = category,
                level = level,
                children = children.map { buildNode(it, level + 1) }
            )
        }

        return rootCategories.map { buildNode(it, 0) }
    }

    private fun flattenCategoryTree(tree: List<CategoryNode>): List<CategoryNode> {
        return tree.flatMap { node ->
            listOf(node) + flattenCategoryTree(node.children)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}