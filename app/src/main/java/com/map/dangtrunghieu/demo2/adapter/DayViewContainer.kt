package com.map.dangtrunghieu.demo2.adapter

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendar.view.ViewContainer
import com.map.dangtrunghieu.demo2.R
import com.map.dangtrunghieu.demo2.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View) : ViewContainer(view) {
     val textView = CalendarDayLayoutBinding.bind(view).calendarDayText

    // With ViewBinding
    // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}