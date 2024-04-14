package com.chirayu.financeapp.model.statsitems

data class TagMovementsSum(
    val tagId: Int,
    val name: String,
    val color: Int,
    val sum: Double,
    val percentage: Double
)
