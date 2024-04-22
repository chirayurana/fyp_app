package com.chirayu.financeapp.util

import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.data.repository.BudgetRepository
import com.chirayu.financeapp.model.entities.Budget
import com.chirayu.financeapp.model.entities.Movement
import com.chirayu.financeapp.model.entities.mapToRemoteBudget
import com.chirayu.financeapp.model.enums.AddToBudgetResult
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.models.RemoteBudget
import com.chirayu.financeapp.network.models.mapToBudget
import com.chirayu.financeapp.network.repository.RemoteBudgetRepository

object BudgetUtil {
    private lateinit var budgetsRepository: RemoteBudgetRepository

    fun init(application: SaveAppApplication) {
        budgetsRepository = application.remoteBudgetRepository
    }

    suspend fun tryAddMovementToBudget(m: Movement, force: Boolean = false): AddToBudgetResult {
        if (m.budgetId == null || m.budgetId == 0) {
            return AddToBudgetResult.SUCCEEDED
        }

        val result = budgetsRepository.getById(m.budgetId!!)
        val budget: Budget? = if(result is NetworkResult.Success) result.data.mapToBudget() else null
        if (budget == null) {
            m.budgetId = 0
            return if (force) AddToBudgetResult.SUCCEEDED else AddToBudgetResult.NOT_EXISTS
        }

        if (!force) {
            if (m.date.isBefore(budget.from) || m.date.isAfter(budget.to)) {
                return AddToBudgetResult.DATE_OUT_OF_RANGE
            }

            if (budget.used >= budget.max) {
                return AddToBudgetResult.BUDGET_EMPTY
            }
        }

        budget.used += m.amount
        budgetsRepository.update(budget.mapToRemoteBudget())

        return AddToBudgetResult.SUCCEEDED
    }

    suspend fun removeMovementFromBudget(m: Movement) {
        if (m.budgetId == null || m.budgetId == 0) {
            return
        }

        val result = budgetsRepository.getById(m.budgetId!!)
        val budget: Budget? = if(result is NetworkResult.Success) result.data.mapToBudget() else null
        budget ?: return

        budget.used -= m.amount
        budgetsRepository.update(budget.mapToRemoteBudget())
    }
}
