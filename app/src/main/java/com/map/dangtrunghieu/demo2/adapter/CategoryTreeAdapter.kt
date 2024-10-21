package com.map.dangtrunghieu.demo2.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.model.Category
import com.map.dangtrunghieu.demo2.model.CategoryNode

class CategoryTreeAdapter(context: Context, private val categories: List<CategoryNode>) :
    ArrayAdapter<CategoryNode>(context, R.layout.item_category, categories) {
    private val indentWidth = context.resources.getDimensionPixelSize(R.dimen.category_indent_width)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val view = recycledView ?: LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        val container = view.findViewById<LinearLayout>(R.id.parentLayout)
        val textView = view.findViewById<TextView>(R.id.text)
        val category = getItem(position)

        category?.let {
            val indentPadding = it.level * indentWidth
            container.setPadding(indentPadding, container.paddingTop, container.paddingRight, container.paddingBottom)
            textView.text = it.category.name
        }

        return view
    }
}