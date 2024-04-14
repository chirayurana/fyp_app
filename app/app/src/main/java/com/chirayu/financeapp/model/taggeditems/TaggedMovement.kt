package com.chirayu.financeapp.model.taggeditems

import androidx.room.TypeConverters
import com.chirayu.financeapp.data.converters.DateConverter
import java.time.LocalDate

@TypeConverters(DateConverter::class)
data class TaggedMovement(
    var id: Int,
    var amount: Double,
    var description: String,
    var date: LocalDate,
    var tagId: Int,
    var tagName: String,
    var tagColor: Int,
    var budgetId: Int?
)
