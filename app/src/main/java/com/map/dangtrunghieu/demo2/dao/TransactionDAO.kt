package com.map.dangtrunghieu.demo2.dao

import android.annotation.SuppressLint
import android.content.Context
import com.map.dangtrunghieu.demo2.model.CatInOut
import com.map.dangtrunghieu.demo2.model.Category
import com.map.dangtrunghieu.demo2.model.InOut
import com.map.dangtrunghieu.demo2.model.Transaction

class TransactionDAO(context: Context) {
    private val dbHelper = DBHelper(context)

    fun getAllTransaction() = dbHelper.getAllTransaction()
    fun getAllTransactionByDay() = dbHelper.getAllTransactionByDay()

    fun addTransaction(
        name: String,
        amount: Double,
        date: String,
        note: String,
        catInOut: CatInOut
    ): Boolean {
        return dbHelper.addTransaction(
            Transaction(
                name = name,
                amount = amount,
                date = date,
                note = note,
                catInOut = catInOut
            )
        )
    }
}