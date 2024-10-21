package com.map.dangtrunghieu.demo2.model

data class CategoryNode(
    val category: Category,
    val level: Int,
    val children: List<CategoryNode> = emptyList()
)