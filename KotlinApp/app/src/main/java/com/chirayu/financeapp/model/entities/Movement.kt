package com.chirayu.financeapp.model.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.chirayu.financeapp.data.converters.DateConverter
import java.time.LocalDate

@Entity(tableName = "movements", indices = [Index(value = ["date"])])
@TypeConverters(DateConverter::class)
data class Movement(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var amount: Double,
    var description: String,
    var date: LocalDate,
    var tagId: Int,
    var budgetId: Int?
)
