package com.map.dangtrunghieu.demo2.dao

import android.content.Context
import com.map.dangtrunghieu.demo2.model.Category
import com.map.dangtrunghieu.demo2.model.InOut

class CatInOutDAO(context: Context) {
    private val db = DBHelper(context)

    fun insertCatInOut(category: Category, type: Int) = db.insertCatInOut(category, type)
}