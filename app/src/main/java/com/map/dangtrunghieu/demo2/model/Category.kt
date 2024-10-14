package com.map.dangtrunghieu.demo2.model

data class Category(
    var id: Int,
    var name: String,
    var icon: String,
    var note: String,
    var parent: Category?
)