package com.map.dangtrunghieu.demo2.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.dao.CategoryDAO
import com.map.dangtrunghieu.demo2.model.Category

class CustomSpinnerAdapter(private val context: Context, private val categoryDAO: CategoryDAO, private val items: MutableList<Category>) :
    BaseAdapter() {

    data class SpinnerItem(val iconRes: Int, val text: String)

    fun submitList(newList: List<Category>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Category {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_category, parent, false)
        val parentText: TextView = view.findViewById(R.id.text)
        val parentIcon: ImageView = view.findViewById(R.id.icon)
        val parentLayout = view.findViewById<LinearLayout>(R.id.parentLayout)
        val item = getItem(position)

        parentText.text = getItem(position).name

//        val subListCategory = categoryDAO.getCategoryByParentId(item.id)
//        for (subCategory in subListCategory) {
//            val newLinearLayout = LinearLayout(context).apply {
//                orientation = LinearLayout.HORIZONTAL
//                layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//            }
//
//            // Create ImageView
//            val imageView = ImageView(context).apply {
//                setImageResource(R.drawable.ic_work) // Replace with your image resource
//                layoutParams = LinearLayout.LayoutParams(
//                    62,
//                    62
//                ).apply {
//                    marginStart = 32
//                    marginEnd = 16 // Add some margin between the ImageView and TextView
//                }
//            }
//
//            // Create TextView
//            val textView = TextView(context).apply {
//                text = subCategory.name // Set your text
//                layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//            }
//
//            // Add ImageView and TextView to the new LinearLayout
//            newLinearLayout.addView(imageView)
//            newLinearLayout.addView(textView)
//
//            // Add the new LinearLayout to the parent layout
//            parentLayout.addView(newLinearLayout)
//        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}