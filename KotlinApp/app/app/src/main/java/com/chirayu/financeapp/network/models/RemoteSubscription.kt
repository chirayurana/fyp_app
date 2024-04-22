package com.chirayu.financeapp.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RemoteSubscription(
    val id : Int?,
    val name : String,
    val owner : String?,
    @SerializedName("renewal_after") val renewalAfter : Int?,
    @SerializedName("last_paid") val lastPaid : String?
) : Serializable
