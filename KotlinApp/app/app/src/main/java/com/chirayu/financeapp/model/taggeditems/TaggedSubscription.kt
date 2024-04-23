package com.chirayu.financeapp.model.taggeditems

import androidx.room.TypeConverters
import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.enums.RenewalType
import java.time.LocalDate

@TypeConverters(DateConverter::class)
data class TaggedSubscription(
    var id: Int,
    var amount: Double,
    var description: String,
    var lastPaid: LocalDate?,
    var nextRenewal: LocalDate,
    var tagId: Int,
    var tagName: String,
    var tagColor: Int
)
