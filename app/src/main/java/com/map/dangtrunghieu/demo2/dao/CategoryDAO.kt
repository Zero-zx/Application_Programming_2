package com.map.dangtrunghieu.demo2.dao

import android.content.Context
import com.map.dangtrunghieu.demo2.model.Category

class CategoryDAO(context: Context) {
    private val db = DBHelper(context)

    fun getCategoryByInOut(type: Int) = db.getCategoryByInOut(type)

    fun getAllCategory() = db.getAllCategory()

    fun insertCategory(name: String, icon: String, note: String, parent: Category): Category =
        db.insertCategory(name, icon, note, parent)
}