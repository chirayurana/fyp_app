package com.chirayu.financeapp.network.repository

import com.chirayu.financeapp.network.BackendAPI
import com.chirayu.financeapp.network.data.handleRemoteRequest
import com.chirayu.financeapp.network.models.RemoteExpense
import kotlin.math.exp

class RemoteExpenseRepository(private val api : BackendAPI) {

    suspend fun retrieveAllExpenses() = handleRemoteRequest {
        api.retrieveAllExpenses()
    }

    suspend fun addExpense(expense: RemoteExpense) = handleRemoteRequest {
        api.addExpense(expense)
    }

    suspend fun retrieveExpense(expenseId : Int) = handleRemoteRequest {
        api.retrieveExpense(expenseId)
    }

    suspend fun updateExpense(expenseId: Int, expense: RemoteExpense) = handleRemoteRequest {
        api.updateExpense(expenseId, expense)
    }

    suspend fun deleteExpense(expenseId : Int) = handleRemoteRequest {
        api.deleteExpense(expenseId)
    }
}