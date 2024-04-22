package com.chirayu.financeapp.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    val username : String,
    val password : String?,
    @SerializedName("confirm_password") val confirmedPassword : String? = null
) : Serializable
