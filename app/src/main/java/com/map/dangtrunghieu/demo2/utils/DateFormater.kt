package com.map.dangtrunghieu.demo2.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateFormatter {
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @RequiresApi(Build.VERSION_CODES.O)
    fun reformatter(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date: LocalDate = LocalDate.parse(inputDate, inputFormatter)
        return date.format(outputFormatter)
    }
}