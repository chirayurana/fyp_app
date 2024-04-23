package com.chirayu.financeapp.network.repository

import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.network.BackendAPI
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.data.handleRemoteRequest
import com.chirayu.financeapp.network.models.RemoteExpense
import com.chirayu.financeapp.network.models.States
import com.chirayu.financeapp.network.models.RemoteIncome
import com.chirayu.financeapp.util.StatsUtil

class RemoteIncomeRepository(private val api : BackendAPI) {

    suspend fun getAll() = handleRemoteRequest {
        api.retrieveAllIncomes()
    }

    suspend fun insert(income: RemoteIncome) = handleRemoteRequest {
        api.addIncome(income)
    }

    suspend fun getById(incomeId : Int) = handleRemoteRequest {
        api.retrieveIncome(incomeId)
    }

    suspend fun update(income: RemoteIncome) = handleRemoteRequest {
        api.updateIncome(income.id!!,income)
    }

    suspend fun delete(incomeId: Int) = handleRemoteRequest {
        api.deleteIncome(incomeId)
    }

    suspend fun getAllTaggedByYear(year : Int): List<RemoteIncome> {
        val result = getAll()
        if(result is NetworkResult.Success) {
            val expenses = result.data
            val dateConverter = DateConverter()
            return expenses.filter { it.date != null && dateConverter.toDate(it.date).year == year}
        }
        return emptyList()
    }

    suspend fun getAllTaggedByYearSorted(year : Int): List<RemoteIncome> {
        val dateConverter = DateConverter()

        return getAllTaggedByYear(year).sortedBy {
            dateConverter.toDate(it.date!!)
        }
    }

    suspend fun getIncomeStates(): States {
        val resultOfAllIncome = getAll()
        if(resultOfAllIncome is NetworkResult.Success) {
            val incomes = resultOfAllIncome.data

            val month = incomes.filter {
                it.date != null && StatsUtil.isThisMonth(it.date)
            }.sumOf { it.amount }

            val year = incomes.filter {
                it.date != null && StatsUtil.isThisYear(it.date)
            }.sumOf { it.amount }

            val life = incomes.sumOf { it.amount }
            return States(month,year,life)
        }

        return States(0.0,0.0,0.0)
    }
}