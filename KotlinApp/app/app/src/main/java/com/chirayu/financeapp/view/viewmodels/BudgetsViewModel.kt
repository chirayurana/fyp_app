package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.model.entities.Budget
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.models.mapToBudget
import kotlinx.coroutines.launch

class BudgetsViewModel(application: Application): AndroidViewModel(application) {
    private val budgetRepository = (application as SaveAppApplication).remoteBudgetRepository

    private val _budgets = MutableLiveData<List<Budget>>(emptyList())
    val budgets: LiveData<List<Budget>> = _budgets

    init {
        viewModelScope.launch {
            val result = budgetRepository.getAll()
            if(result is NetworkResult.Success) {
                _budgets.value = result.data.map {
                    it.mapToBudget()
                }
            }
        }
    }

    private val _pastSectionCollapsed = MutableLiveData(true)
    val pastSectionCollapsed: LiveData<Boolean> = _pastSectionCollapsed

    fun changePastSectionVisibility() {
        _pastSectionCollapsed.value = !_pastSectionCollapsed.value!!
    }
}
