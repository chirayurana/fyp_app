package com.chirayu.financeapp.network.models

import android.graphics.Color
import com.chirayu.financeapp.R
import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.entities.Tag
import com.chirayu.financeapp.model.taggeditems.TaggedSubscription
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate

data class RemoteSubscription(
    val id : Int?,
    val name : String,
    val amount : Double,
    val owner : String?,
    @SerializedName("subscription_type") val subscriptionType : String?,
    @SerializedName("renewal_after") val renewalAfter : Int?,
    @SerializedName("last_paid") val lastPaid : String?
) : Serializable

fun RemoteSubscription.mapToTaggedSubscription(tag : Tag?): TaggedSubscription {
    val dateConverter = DateConverter()
    val lastPaid = dateConverter.toDate(lastPaid?: LocalDate.now().toString())
    val nextRenewal = lastPaid.plusDays(renewalAfter?.toLong()?: 0L)
    return TaggedSubscription(
        id?: 1,
        amount,
        name,
        lastPaid,
        nextRenewal,
        tag?.id?: 0,
        tag?.name?: "",
        tag?.color?: R.color.emerald_200
    )
}