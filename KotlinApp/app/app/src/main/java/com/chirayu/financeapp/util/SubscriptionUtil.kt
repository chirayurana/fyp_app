package com.chirayu.financeapp.util

import com.chirayu.financeapp.R
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.entities.Movement
import com.chirayu.financeapp.model.entities.Subscription
import com.chirayu.financeapp.model.entities.mapToRemoteExpense
import com.chirayu.financeapp.model.enums.RenewalType
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.models.RemoteSubscription
import java.time.LocalDate

object SubscriptionUtil {
    private fun updateNextRenewal(s: Subscription) {
        s.nextRenewal = when (s.renewalType) {
            RenewalType.WEEKLY -> s.nextRenewal.plusDays(7)
            RenewalType.MONTHLY -> s.nextRenewal.plusMonths(1)
            RenewalType.BIMONTHLY -> s.nextRenewal.plusMonths(2)
            RenewalType.QUARTERLY -> s.nextRenewal.plusMonths(3)
            RenewalType.SEMIANNUALLY -> s.nextRenewal.plusMonths(6)
            else -> s.nextRenewal.plusYears(1)
        }
    }

    suspend fun getMovementFromSub(s: RemoteSubscription, description: String,tagId : Int): Movement? {
        val dateConverter = DateConverter()
        val lastPaidDate = dateConverter.toDate(s.lastPaid?: LocalDate.now().toString())
        val newDate = lastPaidDate.plusDays(s.renewalAfter?.toLong()?: 0L)
        if (newDate.isAfter(LocalDate.now())) {
            return null
        }



//        s.lastPaid = s.nextRenewal
//        updateNextRenewal(s)
        return Movement(
            0,
            s.amount,
            description,
            lastPaidDate,
            tagId,
             0
        )
    }

    suspend fun validateSubscriptions(application: SaveAppApplication) {
        val result = application.subscriptionRepository.getAll()
        if(result !is NetworkResult.Success)
            return

        val subscriptions = result.data
        val description = application.getString(R.string.payment_of)

        for (subscription in subscriptions) {
            val tagT = application.tagRepository.getByName(subscription.name)
            var movement = getMovementFromSub(subscription, description,tagT?.id?: 0)
            while (movement != null) {
                BudgetUtil.tryAddMovementToBudget(movement)
                val tag = application.tagRepository.getById(movement.tagId)
                application.movementRepository.insert(movement.mapToRemoteExpense(tag?.name?: ""))
                StatsUtil.addMovementToStat(application, movement)
                movement = getMovementFromSub(subscription, description,tag?.id?: 0)
            }
            application.subscriptionRepository.update(subscription)
        }
    }
}
