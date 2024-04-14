package com.chirayu.financeapp.data.repository

import androidx.annotation.WorkerThread
import com.chirayu.financeapp.data.dao.BudgetDao
import com.chirayu.financeapp.model.entities.Budget
import com.chirayu.financeapp.model.taggeditems.TaggedBudget
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {
    val allBudgets: Flow<List<TaggedBudget>> = budgetDao.getAllTagged()

    suspend fun getAll(): List<Budget> {
        return budgetDao.getAll()
    }

    @WorkerThread
    suspend fun getById(id: Int) : Budget? {
        return budgetDao.getById(id)
    }

    @WorkerThread
    suspend fun insert(budget: Budget) {
        budgetDao.insert(budget)
    }

    @WorkerThread
    suspend fun update(budget: Budget) {
        budgetDao.update(budget)
    }

    @WorkerThread
    suspend fun delete(budget: Budget) {
        budgetDao.delete(budget)
    }
}
