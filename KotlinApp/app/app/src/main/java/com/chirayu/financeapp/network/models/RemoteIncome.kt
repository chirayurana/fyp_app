package com.chirayu.financeapp.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RemoteIncome(
    val id : Int?,
    val amount : Double,
    val description : String?,
    @SerializedName("income_type") val incomeType : String,
    val owner : String?,
    val date : String?
) : Serializable
