package com.map.dangtrunghieu.demo2.model

import java.io.Serializable

data class Category(
    var id: Int,
    var name: String,
    var icon: String,
    var note: String,
    var parent: Category?
): Serializable