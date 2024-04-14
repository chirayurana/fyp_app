package com.chirayu.financeapp.model.enums

enum class Currencies(val id: Int) {
    NPR(0),
    EUR(1),
    USD(2),
    AUD(3),
    CAD(4),
    GBP(5),
    CHF(6),
    JPY(7),
    CNY(8);

    companion object {
        private val map = Currencies.values().associateBy { it.id }
        fun from(id: Int) : Currencies = map[id] ?: EUR
    }

    fun toSymbol() : String {
        return when (this) {
            EUR -> "€"
            GBP -> "£"
            CHF -> "Fr"
            JPY -> "¥"
            CNY -> "¥"
            NPR -> "Rs"
            else -> "$"
        }
    }
}