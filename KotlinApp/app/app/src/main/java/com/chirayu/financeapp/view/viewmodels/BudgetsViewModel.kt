package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.model.entities.Budget
import com.chirayu.financeapp.model.entities.mapToTaggedBudget
import com.chirayu.financeapp.model.taggeditems.TaggedBudget
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.models.mapToBudget
import kotlinx.coroutines.launch

class BudgetsViewModel(application: Application): AndroidViewModel(application) {
    private val budgetRepository = (application as SaveAppApplication).remoteBudgetRepository

    private val _budgets = MutableLiveData<List<TaggedBudget>>(emptyList())
    val budgets: LiveData<List<TaggedBudget>> = _budgets

    init {
        viewModelScope.launch {
            val result = budgetRepository.getAll()
            if(result is NetworkResult.Success) {
                _budgets.value = result.data.map {
                    it.mapToBudget().mapToTaggedBudget()
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
