package com.chirayu.financeapp.network.models

import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.entities.Movement
import com.chirayu.financeapp.model.entities.Tag
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate

data class RemoteIncome(
    val id : Int?,
    val amount : Double,
    val description : String?,
    @SerializedName("income_type") val incomeType : String,
    val owner : String?,
    val date : String?
) : Serializable

fun RemoteIncome.mapToMovement(tag : Tag?) : Movement {
    return Movement(
        id?: 0,
        amount,
        description?: "",
        DateConverter().toDate(date?: LocalDate.now().toString()),
        tag?.id?: 0,
        null
    )
}
