package com.chirayu.financeapp.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.taggeditems.TaggedBudget
import com.chirayu.financeapp.network.models.RemoteBudget
import java.time.LocalDate

@Entity(tableName = "budgets")
@TypeConverters(DateConverter::class)
data class Budget(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var max: Double,
    var used: Double,
    var name: String,
    var from: LocalDate,
    var to: LocalDate
)

fun Budget.mapToRemoteBudget() : RemoteBudget {
    val dateConverter = DateConverter()
    return RemoteBudget(id,name,max,used,dateConverter.toString(to),null,dateConverter.toString(from))
}
//fun Budget.mapToTaggedBudget() : TaggedBudget {
//    return TaggedBudget(id,max,used,name,from,to,tagId,"",1)
//}
