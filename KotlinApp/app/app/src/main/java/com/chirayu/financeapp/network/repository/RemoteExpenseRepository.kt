package com.chirayu.financeapp.network.repository

import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.entities.Movement
import com.chirayu.financeapp.model.entities.Tag
import com.chirayu.financeapp.network.BackendAPI
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.data.handleRemoteRequest
import com.chirayu.financeapp.network.models.RemoteExpense
import com.chirayu.financeapp.network.models.States
import com.chirayu.financeapp.network.models.TagStates
import com.chirayu.financeapp.network.models.mapToMovement
import com.chirayu.financeapp.util.StatsUtil

class RemoteExpenseRepository(private val api : BackendAPI) {

    suspend fun getAll() = handleRemoteRequest {
        api.retrieveAllExpenses()
    }

    suspend fun insert(expense: RemoteExpense) = handleRemoteRequest {
        api.addExpense(expense)
    }

    suspend fun getById(expenseId : Int) = handleRemoteRequest {
        api.retrieveExpense(expenseId)
    }

    suspend fun update(expense: RemoteExpense) = handleRemoteRequest {
        api.updateExpense(expense.id!!, expense)
    }

    suspend fun delete(expense : RemoteExpense) = handleRemoteRequest {
        api.deleteExpense(expense.id?: 0)
    }

    suspend fun getFirstWithTag(tag : Tag) : Movement? {
        val result = getAll()
        if(result is NetworkResult.Success) {
            return result.data.first { it.expenseType == tag.name}.mapToMovement(tag)
        }
        return null
    }

    suspend fun getAllTaggedByYear(year : Int): List<RemoteExpense> {
        val result = getAll()
        if(result is NetworkResult.Success) {
            val expenses = result.data
            val dateConverter = DateConverter()
            return expenses.filter { it.date != null && dateConverter.toDate(it.date).year == year}
        }
        return emptyList()
    }

    suspend fun getAllTaggedByYearSorted(year : Int): List<RemoteExpense> {
        val dateConverter = DateConverter()

        return getAllTaggedByYear(year).sortedBy {
            dateConverter.toDate(it.date!!)
        }
    }

    suspend fun getExpenseStates(): States {
        val resultOfAllExpenses = getAll()
        if(resultOfAllExpenses is NetworkResult.Success) {
            val expenses = resultOfAllExpenses.data

            val month = expenses.filter {
                it.date != null && StatsUtil.isThisMonth(it.date)
            }.sumOf { it.amount }

            val year = expenses.filter {
                it.date != null && StatsUtil.isThisYear(it.date)
            }.sumOf { it.amount }

            val life = expenses.sumOf { it.amount }
            return States(month,year,life)
        }

        return States(0.0,0.0,0.0)
    }

    suspend fun getHighestTagPerMonth(): TagStates {
        val result = getAll()
        if(result is NetworkResult.Success) {
            val expenses = result.data

            val map = mutableMapOf<String,Double>()
            expenses.filter{
                it.date != null && StatsUtil.isThisMonth(it.date)
            }.forEach {
                map[it.expenseType] = map.getOrDefault(it.expenseType,0.0) + it.amount
            }

            var highestTag = ""
            var highestValue = 0.0
            map.keys.forEach {
                if(map[it]!!>highestValue) {
                    highestTag = it
                    highestValue = map[it]!!
                }
            }
            return TagStates(highestTag,highestValue)
        }

        return TagStates("",0.0)
    }

    suspend fun getHighestTagPerYear(): TagStates {
        val result = getAll()
        if(result is NetworkResult.Success) {
            val expenses = result.data

            val map = mutableMapOf<String,Double>()
            expenses.filter{
                it.date != null && StatsUtil.isThisYear(it.date)
            }.forEach {
                map[it.expenseType] = map.getOrDefault(it.expenseType,0.0) + it.amount
            }

            var highestTag = ""
            var highestValue = 0.0
            map.keys.forEach {
                if(map[it]!!>highestValue) {
                    highestTag = it
                    highestValue = map[it]!!
                }
            }
            return TagStates(highestTag,highestValue)
        }

        return TagStates("",0.0)
    }

    suspend fun getHighestTagPerLife(): TagStates {
        val result = getAll()
        if(result is NetworkResult.Success) {
            val expenses = result.data

            val map = mutableMapOf<String,Double>()
            expenses.forEach {
                map[it.expenseType] = map.getOrDefault(it.expenseType,0.0) + it.amount
            }

            var highestTag = ""
            var highestValue = 0.0
            map.keys.forEach {
                if(map[it]!!>highestValue) {
                    highestTag = it
                    highestValue = map[it]!!
                }
            }
            return TagStates(highestTag,highestValue)
        }

        return TagStates("",0.0)
    }
}