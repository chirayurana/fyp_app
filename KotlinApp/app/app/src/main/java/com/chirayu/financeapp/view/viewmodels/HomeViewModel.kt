package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chirayu.financeapp.R
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.data.repository.TagRepository
import com.chirayu.financeapp.model.entities.Tag
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.util.SettingsUtil
import com.chirayu.financeapp.util.SharedPreferencesManager
import com.chirayu.financeapp.util.StatsUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val saveAppApplication = application as SaveAppApplication

    private val tagRepository: TagRepository = saveAppApplication.tagRepository
    private val incomeRepository = saveAppApplication.incomeRepository
    private val movementRepository = saveAppApplication.movementRepository
    private val userRepository = saveAppApplication.userRepository

    private val months: Map<Int, MutableLiveData<Double>> = StatsUtil.monthTags
    private val years: Map<Int, MutableLiveData<Double>> = StatsUtil.yearTags
    private val life: Map<Int, MutableLiveData<Double>> = StatsUtil.lifeTags

    private val defaultTag: Tag = Tag(0, "", R.color.emerald_500, false)

    private val _symbol = MutableLiveData(Currencies.EUR.toSymbol())
    val symbol: LiveData<String> = _symbol

    val zero = 0.0

    // Month data
    val currentMonth: LiveData<String> = MutableLiveData(setMonth())

    private val _monthSummary = MutableLiveData(0.0)
    val monthSummary: LiveData<Double> = _monthSummary

    private var _monthExpenses = MutableLiveData(0.0)
    val monthExpenses: LiveData<Double> = _monthExpenses

    private var _monthIncomes = MutableLiveData(0.0)
    val monthIncomes: LiveData<Double> = _monthIncomes

    private val _monthHighestTag = MutableLiveData(defaultTag)
    val monthHighestTag: LiveData<Tag?> = _monthHighestTag

    private val _monthHighestTagValue = MutableLiveData(-1.0)
    val monthHighestTagValue: LiveData<Double> = _monthHighestTagValue

    // Year data
    val currentYear: LiveData<String> = MutableLiveData(LocalDate.now().year.toString())

    private val _yearSummary = MutableLiveData(0.0)
    val yearSummary: MutableLiveData<Double> = _yearSummary

    private var _yearExpenses = MutableLiveData(0.0)
    val yearExpenses: LiveData<Double> = _yearExpenses

    private var _yearIncomes = MutableLiveData(0.0)
    val yearIncomes: LiveData<Double> = _yearIncomes

    private val _yearHighestTag = MutableLiveData(defaultTag)
    val yearHighestTag: LiveData<Tag?> = _yearHighestTag

    private val _yearHighestTagValue = MutableLiveData(-1.0)
    val yearHighestTagValue: LiveData<Double> = _yearHighestTagValue

    // Life data
    private val _lifeNetWorth = MutableLiveData(0.0)
    val lifeNetWorth: LiveData<Double> = _lifeNetWorth

    private val _lifeExpenses = MutableLiveData(0.0)
    val lifeExpenses: LiveData<Double> = _lifeExpenses

    private var _lifeIncomes = MutableLiveData(0.0)
    val lifeIncomes: LiveData<Double> = _lifeIncomes

    private val _lifeHighestTag = MutableLiveData(defaultTag)
    val lifeHighestTag: LiveData<Tag?> = _lifeHighestTag

    private val _lifeHighestTagValue = MutableLiveData(-1.0)
    val lifeHighestTagValue: LiveData<Double> = _lifeHighestTagValue

    //user
    private var _isUserLoggedIn = MutableLiveData(true)
    val isUserLoggedIn = _isUserLoggedIn

    init {
        viewModelScope.launch {
            val userResult = userRepository.userDetails()
            if (userResult !is NetworkResult.Success) {
                saveAppApplication.sharedPreferencesManager.removePreference(
                    SharedPreferencesManager.TOKEN_KEY
                )
                _isUserLoggedIn.value = false
            }
            _symbol.value = Currencies.from(SettingsUtil.getCurrency().first()).toSymbol()
            incomeRepository.getIncomeStates().let {
                _monthIncomes.value = it.monthState
                _yearIncomes.value = it.yearState
                _lifeIncomes.value = it.lifeState
            }
            movementRepository.getExpenseStates().let {
                _monthExpenses.value = it.monthState
                _yearExpenses.value = it.yearState
                _lifeExpenses.value = it.lifeState
            }
        }

        setupMonth()
        setupYear()
        setupLife()

        observeTags()
    }

    private fun setMonth(): String {
        return when (LocalDate.now().monthValue) {
            1 -> saveAppApplication.getString(R.string.january)
            2 -> saveAppApplication.getString(R.string.february)
            3 -> saveAppApplication.getString(R.string.march)
            4 -> saveAppApplication.getString(R.string.april)
            5 -> saveAppApplication.getString(R.string.may)
            6 -> saveAppApplication.getString(R.string.june)
            7 -> saveAppApplication.getString(R.string.july)
            8 -> saveAppApplication.getString(R.string.august)
            9 -> saveAppApplication.getString(R.string.september)
            10 -> saveAppApplication.getString(R.string.october)
            11 -> saveAppApplication.getString(R.string.november)
            else -> saveAppApplication.getString(R.string.december)
        }
    }

    private fun setupMonth() {
        monthExpenses.observeForever {
            _monthSummary.value = monthIncomes.value!! - it
        }
        monthIncomes.observeForever {
            _monthSummary.value = it - monthExpenses.value!!
        }

        runBlocking {
            val tagStates = movementRepository.getHighestTagPerMonth()
            _monthHighestTag.value = tagRepository.getByName(tagStates.name)
            _monthHighestTagValue.value = tagStates.value
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (val result = userRepository.logout()) {
                is NetworkResult.Error -> {
                    if (result.code == 200) {
                        removeToken()
                    }
                }

                is NetworkResult.Exception -> Log.d(
                    "LoginViewModel",
                    "Exception to logout ${result.e.message ?: ""}"
                )

                is NetworkResult.Success -> {
                    removeToken()
                }
            }
        }
    }

    private fun removeToken() {
        val sharedPreferencesManager = saveAppApplication.sharedPreferencesManager
        sharedPreferencesManager.removePreference(
            SharedPreferencesManager.TOKEN_KEY
        )
        _isUserLoggedIn.value = false
    }

    private fun setupYear() {
        yearExpenses.observeForever {
            _yearSummary.value = yearIncomes.value!! - it
        }
        yearIncomes.observeForever {
            _yearSummary.value = it - yearExpenses.value!!
        }

        runBlocking {
            val tagStates = movementRepository.getHighestTagPerYear()
            _yearHighestTag.value = tagRepository.getByName(tagStates.name)
            _yearHighestTagValue.value = tagStates.value
        }
    }

    private fun setupLife() {
        lifeExpenses.observeForever {
            _lifeNetWorth.value = lifeIncomes.value!! - it
        }
        lifeIncomes.observeForever {
            _lifeNetWorth.value = it - lifeExpenses.value!!
        }

        runBlocking {
            val tagStates = movementRepository.getHighestTagPerYear()
            _lifeHighestTag.value = tagRepository.getByName(tagStates.name)
            _lifeHighestTagValue.value = tagStates.value
        }
    }

    private fun observeTags() {
        for (i: Int in months.keys) {
            months[i]!!.observeForever {
                if (it > _monthHighestTagValue.value!!) {
                    _monthHighestTag.value = runBlocking { tagRepository.getById(i) }
                    _monthHighestTagValue.value = it
                }
            }
        }
        for (i: Int in years.keys) {
            years[i]!!.observeForever {
                if (it > _yearHighestTagValue.value!!) {
                    _yearHighestTag.value = runBlocking { tagRepository.getById(i) }
                    _yearHighestTagValue.value = it
                }
            }
        }
        for (i: Int in life.keys) {
            life[i]!!.observeForever {
                if (it > lifeHighestTagValue.value!!) {
                    _lifeHighestTag.value = runBlocking { tagRepository.getById(i) }
                    _lifeHighestTagValue.value = it
                }
            }
        }
    }
}
