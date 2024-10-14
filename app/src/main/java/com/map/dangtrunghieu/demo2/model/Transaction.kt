package com.map.dangtrunghieu.demo2.model

data class Transaction(
    var id: Int = 0,
    var name: String,
    var catInOut: CatInOut,
    var amount: Double,
    var date: String,
    var note: String
)