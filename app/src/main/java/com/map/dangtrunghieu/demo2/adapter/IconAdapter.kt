package com.map.dangtrunghieu.demo2.adapter


import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.model.CategoryNode

class IconAdapter(context: Context, private val icons: List<Int>) :
    ArrayAdapter<Int>(context, R.layout.item_icon, icons) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val view = recycledView ?: LayoutInflater.from(context).inflate(R.layout.item_icon, parent, false)
        val container = view.findViewById<LinearLayout>(R.id.parentLayout)
        val icon = view.findViewById<ImageView>(R.id.icon)

        val iconRes = getItem(position)
        if (iconRes != null) {
            icon.setImageResource(iconRes)
        }

        return view
    }
}