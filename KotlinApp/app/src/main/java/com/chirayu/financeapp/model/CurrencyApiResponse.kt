package com.chirayu.financeapp.model

data class CurrencyApiResponse(
    val amount: Int,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
