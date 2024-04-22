package com.chirayu.financeapp.network.repository

import com.chirayu.financeapp.network.BackendAPI
import com.chirayu.financeapp.network.data.handleRemoteRequest
import com.chirayu.financeapp.network.models.RemoteBudget

class RemoteBudgetRepository(private val api : BackendAPI) {

    suspend fun getAll() = handleRemoteRequest {
        api.retrieveAllBudgets()
    }

    suspend fun insert(budget: RemoteBudget) = handleRemoteRequest {
        api.addBudget(budget)
    }

    suspend fun getById(budgetId : Int) = handleRemoteRequest {
        api.retrieveBudget(budgetId)
    }

    suspend fun update(budget: RemoteBudget) = handleRemoteRequest {
        api.updateBudget(budget.id!!,budget)
    }

    suspend fun delete(budget: RemoteBudget) = handleRemoteRequest {
        api.deleteBudget(budget.id!!)
    }
}