package com.map.dangtrunghieu.demo2.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.adapter.CategoryTreeAdapter
import com.map.dangtrunghieu.demo2.adapter.IconAdapter
import com.map.dangtrunghieu.demo2.dao.CatInOutDAO
import com.map.dangtrunghieu.demo2.dao.CategoryDAO
import com.map.dangtrunghieu.demo2.databinding.ActivityAddCategoryBinding
import com.map.dangtrunghieu.demo2.databinding.ActivityMainBinding
import com.map.dangtrunghieu.demo2.model.Category
import com.map.dangtrunghieu.demo2.model.CategoryNode
import com.map.dangtrunghieu.demo2.utils.IconHelper

class AddCategoryActivity : AppCompatActivity(),  AdapterView.OnItemSelectedListener {
    private var _binding: ActivityAddCategoryBinding? = null
    private val binding get() = _binding!!
    private val categoryDAO = CategoryDAO(this)
    private val catInOutDAO = CatInOutDAO(this)
    private lateinit var categoryAdapter: CategoryTreeAdapter
    private val iconAdapter by lazy { IconAdapter(this, IconHelper.iconList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        setUpOnClickListener()
    }

    private fun initComponents() {
        val categoryTree = buildCategoryTree(categoryDAO.getCategoryByInOut(0))
        val flattenedTree = flattenCategoryTree(categoryTree)
        categoryAdapter = CategoryTreeAdapter(this, flattenedTree)
        binding.spParent.adapter = categoryAdapter
        binding.spIcon.adapter = iconAdapter

        val inOutList = listOf("In", "Out")
        val inOutAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, inOutList)
        binding.spInOut.adapter = inOutAdapter
    }

    private fun setUpOnClickListener() {

        binding.spInOut.onItemSelectedListener = this

        binding.btnAdd.setOnClickListener {
            val parent = binding.spParent.selectedItem as CategoryNode
            val icon = binding.spIcon.selectedItem
            val name = binding.etName.text.toString()


            val resultIntent = Intent()
            val category = categoryDAO.insertCategory(
                name = name,
                icon = icon.toString(),
                note = "",
                parent = parent.category
            )
            catInOutDAO.insertCatInOut(category, binding.spInOut.selectedItemPosition)
            resultIntent.putExtra(
                "result", category
            )
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id) {
            R.id.spInOut -> {
                val inOut = parent.getItemAtPosition(position) as String
                val categoryTree = if (inOut == "In") {
                    buildCategoryTree(categoryDAO.getCategoryByInOut(0))
                } else {
                    buildCategoryTree(categoryDAO.getCategoryByInOut(1))
                }
                val flattenedTree = flattenCategoryTree(categoryTree)
                categoryAdapter = CategoryTreeAdapter(this, flattenedTree)
                binding.spParent.adapter = categoryAdapter
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}