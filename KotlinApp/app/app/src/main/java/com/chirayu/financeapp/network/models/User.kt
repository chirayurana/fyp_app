package com.chirayu.financeapp.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    val username : String,
    val password : String?,
    @SerializedName("confirm_password") val confirmedPassword : String? = null,
    @SerializedName("date_joined") val dateJoined : String? = null,
    @SerializedName("total_balance") val totalBalance : Double? = 0.0
) : Serializable
