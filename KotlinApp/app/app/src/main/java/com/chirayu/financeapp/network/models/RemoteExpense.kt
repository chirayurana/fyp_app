package com.chirayu.financeapp.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RemoteExpense(
    val id : Int?,
    val amount : Double,
    val description : String?,
    @SerializedName("expense_type") val expenseType : String?,
    val owner : String?,
    val date : String?
): Serializable
