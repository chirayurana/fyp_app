package com.chirayu.financeapp.network.models

import android.graphics.Color
import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.entities.Movement
import com.chirayu.financeapp.model.entities.Tag
import com.chirayu.financeapp.model.taggeditems.TaggedMovement
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDate

data class RemoteExpense(
    val id: Int?,
    val amount: Double,
    val description: String,
    @SerializedName("expense_type") val expenseType: String,
    val owner: String?,
    val date: String?
) : Serializable

fun RemoteExpense.mapToTaggedMovement(tag: Tag?): TaggedMovement {
    return TaggedMovement(
        id ?: 0,
        amount,
        description ?: "",
        DateConverter().toDate(date ?: LocalDate.now().toString()),
        tag?.id ?: 0,
        tag?.name ?: "",
        tag?.color ?: Color.WHITE,
        null
    )
}

fun RemoteExpense.mapToMovement(tag: Tag?): Movement {
    return Movement(
        id ?: 0,
        amount,
        description ?: "",
        DateConverter().toDate(date ?: LocalDate.now().toString()),
        tag?.id?: 0,
        null
    )
}
