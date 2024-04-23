package com.chirayu.financeapp.network.models

import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.entities.Budget
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate

data class RemoteBudget(
    val id : Int?,
    val name : String,
    @SerializedName("max_limit") val maxLimit : Double,
    @SerializedName("current_spent") val currentSpent : Double?,
    @SerializedName("expiry_at") val expiryAt : String?,
    val owner : String?,
    val date : String?
) : Serializable

fun RemoteBudget.mapToBudget() : Budget{
    val dateConverter = DateConverter()
    val from = if(date!= null) dateConverter.toDate(date) else LocalDate.now()
    val to = if(expiryAt!= null) dateConverter.toDate(expiryAt) else from
    return Budget(id?: 0, maxLimit,currentSpent?: 0.0,name,from,to)
}
