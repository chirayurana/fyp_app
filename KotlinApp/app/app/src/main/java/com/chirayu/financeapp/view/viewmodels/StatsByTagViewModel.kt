package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.chirayu.financeapp.R
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.model.entities.Tag
import com.chirayu.financeapp.model.statsitems.TagMovementsSum
import com.chirayu.financeapp.model.taggeditems.TaggedMovement
import com.chirayu.financeapp.network.models.RemoteIncome
import com.chirayu.financeapp.network.models.mapToTaggedMovement
import com.chirayu.financeapp.util.TagUtil
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatsByTagViewModel(application: Application) : AndroidViewModel(application) {
    private val _app = application as SaveAppApplication

    private val _tags: LiveData<List<Tag>> = _app.tagRepository.allTags.asLiveData()

    private var _movements: List<TaggedMovement> = listOf()
    private var _incomes: List<RemoteIncome> = listOf()

    private val _tagSums: MutableMap<Int, Double> = mutableMapOf()

    private var _setLabel: String = application.getString(R.string.expenses_by_tag)

    private val _isShowingExpenses: MutableLiveData<Boolean> = MutableLiveData(true)

    private val _tagSumItems: MutableLiveData<List<TagMovementsSum>> = MutableLiveData(listOf())
    val tagSumItems get(): LiveData<List<TagMovementsSum>> = _tagSumItems

    private val _year: MutableLiveData<String> = MutableLiveData(LocalDate.now().year.toString())
    val year: LiveData<String> = _year

    private val _showEmptyMessage: MutableLiveData<Boolean> = MutableLiveData(false)
    val showEmptyMessage: LiveData<Boolean> = _showEmptyMessage

    var years: List<String> = listOf()

    var dataSet: PieDataSet = PieDataSet(listOf(), _setLabel)

    var onMovementsChangeCallback: () -> Unit = { }

    init {
        initYears()

        _tags.observeForever { tags ->
            tags.let {
                _tagSums.clear()
                tags.forEach {
                    if (_isShowingExpenses.value!! xor it.isIncome)
                        _tagSums[it.id] = 0.0
                }
            }
            viewModelScope.launch {
                calculateSums(_isShowingExpenses.value!!)
            }
        }
        _year.observeForever { value ->
            viewModelScope.launch {
                _movements = _app.movementRepository.getAllTaggedByYear(value.toInt()).map {
                    val tag = _app.tagRepository.getByName(it.expenseType ?: "")
                    it.mapToTaggedMovement(tag)
                }
                _incomes = _app.incomeRepository.getAllTaggedByYear(year.value!!.toInt())
                _showEmptyMessage.value = _movements.isEmpty()
                _tagSums.keys.forEach {
                    _tagSums[it] = 0.0
                }
                calculateSums(_isShowingExpenses.value!!)
            }
        }
        _isShowingExpenses.observeForever { value ->
            _setLabel = if (value)
                application.getString(R.string.expenses_by_tag)
            else
                application.getString(R.string.incomes_by_tag)

            viewModelScope.launch {
                _movements = _app.movementRepository.getAllTaggedByYear(year.value!!.toInt()).map {
                    val tag = _app.tagRepository.getByName(it.expenseType ?: "")
                    it.mapToTaggedMovement(tag)
                }
                _incomes = _app.incomeRepository.getAllTaggedByYear(year.value!!.toInt())
                _showEmptyMessage.value = _movements.isEmpty()
                _tagSums.clear()
                _tags.value?.forEach {
                    if (value xor it.isIncome)
                        _tagSums[it.id] = 0.0
                }
                calculateSums(value)
            }
        }
    }

    // Methods
    fun setYear(value: String) {
        _year.value = value
    }

    fun setType(isExpenses: Boolean) {
        _isShowingExpenses.value = isExpenses
    }

    private suspend fun calculateSums(selectExpenses: Boolean) {
        if (selectExpenses) {
            _movements.forEach {
                _tagSums[it.tagId] = (_tagSums[it.tagId] ?: 0.0) + it.amount
            }
        } else {
            _incomes.forEach {
                val tag = _app.tagRepository.getByName(it.incomeType)
                _tagSums[tag?.id?: 0] = (_tagSums[tag?: 0] ?: 0.0) + it.amount
            }
        }

        updateEntries(selectExpenses)
    }

    private fun updateEntries(selectExpenses: Boolean) {
        var generalSum = 0.0
        _tagSums.values.forEach { generalSum += it }

        val entries: MutableList<PieEntry> = mutableListOf()
        val items: MutableList<TagMovementsSum> = mutableListOf()
        val colors: MutableList<Int> = mutableListOf()
        _tags.value?.forEach {
            val sum = _tagSums[it.id] ?: 0.0
            val percentage = if (generalSum != 0.0) sum * 100.0 / generalSum else 0.0
            if (selectExpenses xor it.isIncome) {
                items.add(TagMovementsSum(it.id, it.name, it.color, sum, percentage))
            }

            if (sum != 0.0) {
                colors.add(_app.getColor(it.color))
                entries.add(PieEntry(percentage.toFloat(), it.name))
            }
        }

        _tagSumItems.value = items.toList()

        dataSet = PieDataSet(entries, _setLabel)
        dataSet.colors = colors
        dataSet.valueFormatter = PercentFormatter()

        onMovementsChangeCallback()
    }

    private fun initYears() {
        val values = mutableStateListOf<String>()
        val currentYear = LocalDate.now().year
        for (i: Int in 0 until 7) {
            values.add((currentYear - i).toString())
        }

        years = values.toList()
    }
}
