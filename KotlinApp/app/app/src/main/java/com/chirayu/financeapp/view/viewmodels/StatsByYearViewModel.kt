package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.statsitems.YearStats
import com.chirayu.financeapp.model.taggeditems.TaggedMovement
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.models.RemoteIncome
import com.chirayu.financeapp.network.models.mapToTaggedMovement
import com.chirayu.financeapp.util.TagUtil
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatsByYearViewModel(application: Application) : AndroidViewModel(application) {
    private val _app = application as SaveAppApplication

    private val _movements: MutableLiveData<List<TaggedMovement>> = MutableLiveData()
    private var _incomes: MutableLiveData<List<RemoteIncome>> = MutableLiveData()

    private val _expensesByYear: MutableMap<Int, Double> = mutableMapOf()
    private val _incomesByYear: MutableMap<Int, Double> = mutableMapOf()

    private val _yearStatsItems: MutableLiveData<List<YearStats>> = MutableLiveData(listOf())
    val yearStatsItems get(): LiveData<List<YearStats>> = _yearStatsItems

    init {
        viewModelScope.launch {
            val result = _app.movementRepository.getAll()
            if(result is NetworkResult.Success) {
                _movements.value = result.data.map {
                    val tag = _app.tagRepository.getByName(it.expenseType)
                    it.mapToTaggedMovement(tag)
                }
            }
            val incomeResults = _app.incomeRepository.getAll()
            if(incomeResults is NetworkResult.Success)
                _incomes.value = incomeResults.data
        }

        _movements.observeForever(Observer { movements ->
            _expensesByYear.keys.forEach {
                _expensesByYear[it] = 0.0
                _incomesByYear[it] = 0.0
            }
            movements?.let {
                calculateSums()
            }
            updateEntries()
        })

        _incomes.observeForever(Observer { movements ->
            _expensesByYear.keys.forEach {
                _expensesByYear[it] = 0.0
                _incomesByYear[it] = 0.0
            }
            movements?.let {
                calculateSums()
            }
            updateEntries()
        })
    }

    // Methods
    private fun calculateSums() {
        _movements.value?.forEach {
            val year = it.date.year
            _expensesByYear[year] = (_expensesByYear[year] ?: 0.0) + (it.amount * 1.0)
        }

        _incomes.value?.forEach {
            val year = DateConverter().toDate(it.date?: LocalDate.now().toString()).year
            _incomesByYear[year] = (_incomesByYear[year] ?: 0.0) + (it.amount * 1.0)
        }
    }

    private fun updateEntries() {
        val items: MutableList<YearStats> = mutableListOf()

        _incomesByYear.keys.forEach {
            items.add(0, YearStats(it, _incomesByYear[it] ?: 0.0, _expensesByYear[it] ?: 0.0))
        }

        _yearStatsItems.value = items.toList()
    }
}
