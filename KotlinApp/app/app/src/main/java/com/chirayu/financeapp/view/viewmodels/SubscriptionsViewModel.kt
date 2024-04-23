package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.model.taggeditems.TaggedSubscription
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.models.RemoteSubscription
import com.chirayu.financeapp.network.models.mapToTaggedSubscription
import kotlinx.coroutines.launch

class SubscriptionsViewModel(application: Application) : AndroidViewModel(application) {
    private val subscriptionRepository = (application as SaveAppApplication).subscriptionRepository
    private val tagRepository = (application as SaveAppApplication).tagRepository

    val subscriptions: MutableLiveData<List<TaggedSubscription>> = MutableLiveData()


    private val _symbol = MutableLiveData(Currencies.EUR.toSymbol())
    val symbol: LiveData<String> = _symbol

    private val _activeSubscriptionsCount: MutableLiveData<Int> = MutableLiveData(0)
    val activeSubscriptionsCount: LiveData<Int> = _activeSubscriptionsCount

    private val _monthlyExpense: MutableLiveData<Double> = MutableLiveData(0.0)
    val monthlyExpense: LiveData<Double> = _monthlyExpense

    private val _yearlyExpense: MutableLiveData<Double> = MutableLiveData(0.0)
    val yearlyExpense: LiveData<Double> = _yearlyExpense

    init {
        viewModelScope.launch {
            val result = subscriptionRepository.getAll()
            if(result is NetworkResult.Success)
                subscriptions.value = result.data.map {
                    val tag = tagRepository.getByName(it.subscriptionType?: "")
                    it.mapToTaggedSubscription(tag)
                }
        }
        subscriptions.observeForever { subscriptions ->
            subscriptions.let {
                _activeSubscriptionsCount.value = subscriptions.size

                var sum = 0.0
                subscriptions.forEach {
                    sum += it.amount
                }

                _monthlyExpense.value = sum / 12.0
                _yearlyExpense.value = sum
            }
        }
    }

    // Methods
    fun setSymbol(value: String) {
        _symbol.value = value
    }
}
