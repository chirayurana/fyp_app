package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.model.taggeditems.TaggedSubscription

class SubscriptionsViewModel(application: Application) : AndroidViewModel(application) {
    private val subscriptionRepository = (application as SaveAppApplication).subscriptionRepository

    val subscriptions: LiveData<List<TaggedSubscription>> =
        subscriptionRepository.allTaggedSubscriptions.asLiveData()

    private val _symbol = MutableLiveData(Currencies.EUR.toSymbol())
    val symbol: LiveData<String> = _symbol

    private val _activeSubscriptionsCount: MutableLiveData<Int> = MutableLiveData(0)
    val activeSubscriptionsCount: LiveData<Int> = _activeSubscriptionsCount

    private val _monthlyExpense: MutableLiveData<Double> = MutableLiveData(0.0)
    val monthlyExpense: LiveData<Double> = _monthlyExpense

    private val _yearlyExpense: MutableLiveData<Double> = MutableLiveData(0.0)
    val yearlyExpense: LiveData<Double> = _yearlyExpense

    init {
        subscriptions.observeForever { subscriptions ->
            subscriptions.let {
                _activeSubscriptionsCount.value = subscriptions.size

                var sum = 0.0
                subscriptions.forEach {
                    sum += it.amount * it.renewalType.multiplier
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
