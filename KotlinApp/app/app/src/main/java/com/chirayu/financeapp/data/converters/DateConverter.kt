package com.chirayu.financeapp.data.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DateConverter {
    @TypeConverter
    fun toDate(date: String): LocalDate {

        return try {
            LocalDate.parse(date)
        } catch (e: DateTimeParseException) {
            if(date.contains("T")) {
                val newDate = date.subSequence(0,date.indexOf("T"))
                return LocalDate.parse(newDate)
            }
            return LocalDate.now()
        }
    }

    @TypeConverter
    fun toString(date: LocalDate): String {
        return date.toString()
    }
}
