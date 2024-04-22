package com.chirayu.financeapp.network.repository

import com.chirayu.financeapp.network.BackendAPI
import com.chirayu.financeapp.network.data.handleRemoteRequest
import com.chirayu.financeapp.network.models.RemoteBudget

class RemoteIncomeRepository(private val api : BackendAPI) {

    suspend fun retrieveAllIncomes() = handleRemoteRequest {
        api.retrieveAllIncomes()
    }

    suspend fun addBudget(budget: RemoteBudget) = handleRemoteRequest {
        api.addBudget(budget)
    }

    suspend fun retrieveBudget(budgetId : Int) = handleRemoteRequest {
        api.retrieveBudget(budgetId)
    }

    suspend fun updateBudget(budgetId: Int,budget: RemoteBudget) = handleRemoteRequest {
        api.updateBudget(budgetId,budget)
    }

    suspend fun deleteBudget(budgetId: Int) = handleRemoteRequest {
        api.deleteBudget(budgetId)
    }
}