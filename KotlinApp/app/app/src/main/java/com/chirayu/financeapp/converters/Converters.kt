package com.chirayu.financeapp.converters

import androidx.databinding.InverseMethod
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.model.enums.RenewalType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Converters {
    @InverseMethod("tickerToCurrency")
    @JvmStatic
    fun currencyToTicker(value: Currencies?): String {
        return value?.name ?: Currencies.EUR.name
    }

    @JvmStatic
    fun tickerToCurrency(value: String): Currencies {
        return when (value) {
            "NPR" -> Currencies.NPR
            "EUR" -> Currencies.EUR
            "AUD" -> Currencies.AUD
            "CAD" -> Currencies.CAD
            "GBP" -> Currencies.GBP
            "CHF" -> Currencies.CHF
            "JPY" -> Currencies.JPY
            "CNY" -> Currencies.CNY
            else -> Currencies.USD
        }
    }

    @InverseMethod("stringToDate")
    @JvmStatic
    fun dateToString(value: LocalDate?): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return value?.format(formatter) ?: LocalDate.now().format(formatter)
    }

    @JvmStatic
    fun stringToDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @InverseMethod("stringToRenewal")
    @JvmStatic
    fun renewalToString(value: RenewalType?): String {
        return value?.name ?: RenewalType.WEEKLY.name
    }

    @JvmStatic
    fun stringToRenewal(value: String): RenewalType {
        return when (value) {
            "WEEKLY" -> RenewalType.WEEKLY
            "MONTHLY" -> RenewalType.MONTHLY
            "BIMONTHLY" -> RenewalType.BIMONTHLY
            "QUARTERLY" -> RenewalType.QUARTERLY
            "SEMIANNUALLY" -> RenewalType.SEMIANNUALLY
            else -> RenewalType.YEARLY
        }
    }
}