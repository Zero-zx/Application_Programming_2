package com.map.dangtrunghieu.demo2.dao

import android.content.Context
import com.map.dangtrunghieu.demo2.model.Category

class CategoryDAO(context: Context) {
    private val db = DBHelper(context)

    fun getCategoryByInOut(type: Int) = db.getCategoryByInOut(type)
//    fun getCategoryByName(name: String) = db.getCategoryByName(name)
//    fun getInoutById(id: Int) = db.getInOutById(id)
//    fun getCategoryByParentId(parentId: Int) = db.getCategoryByParentId(parentId)
}