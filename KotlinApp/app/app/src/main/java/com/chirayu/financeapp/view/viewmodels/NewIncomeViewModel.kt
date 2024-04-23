package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chirayu.financeapp.BR
import com.chirayu.financeapp.MainActivity
import com.chirayu.financeapp.R
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.converters.Converters
import com.chirayu.financeapp.data.converters.DateConverter
import com.chirayu.financeapp.model.entities.Tag
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.models.RemoteIncome
import com.chirayu.financeapp.util.CurrencyUtil
import com.chirayu.financeapp.util.SettingsUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class NewIncomeViewModel(application: Application) : AndroidViewModel(application), Observable {
    private val saveAppApplication = application as SaveAppApplication
    private val incomeRepository = saveAppApplication.incomeRepository

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    val baseCurrency: Currencies =
        runBlocking { Currencies.from(SettingsUtil.getCurrency().first()) }

    private var _amount: String = ""
    private var _currency: Currencies = baseCurrency
    private var _description: String = ""
    private var _date = LocalDate.now()
    private var _tag: Tag? = null

    var onAmountChanged: () -> Unit = { }
    var onDescriptionChanged: () -> Unit = { }
    var validateTag: () -> Unit = { }

    val tags: MutableLiveData<Array<Tag>> = MutableLiveData<Array<Tag>>()

    val currencies: MutableLiveData<Array<Currencies>> =
        MutableLiveData<Array<Currencies>>(Currencies.values())

    init {
        viewModelScope.launch {
            tags.value = saveAppApplication.tagRepository.allTags.first().filter {
                it.isIncome
            }.toTypedArray()
        }
    }

    // Bindings
    @Bindable
    fun getAmount(): String {
        return _amount
    }

    fun setAmount(value: String) {
        if (value == _amount) {
            return
        }

        _amount = value
        notifyPropertyChanged(BR.amount)
        onAmountChanged.invoke()
    }

    @Bindable
    fun getCurrency(): Currencies {
        return _currency
    }

    fun setCurrency(value: Currencies) {
        if (value == _currency) {
            return
        }

        _currency = value
        notifyPropertyChanged(BR.currency)
    }

    @Bindable
    fun getDescription(): String {
        return _description
    }

    fun setDescription(value: String) {
        if (value == _description) {
            return
        }

        _description = value
        notifyPropertyChanged(BR.description)
        onDescriptionChanged.invoke()
    }

    @Bindable
    fun getDate(): LocalDate {
        return _date
    }

    fun setDate(value: LocalDate) {
        val v = Converters.dateToString(value)
        if (value == _date) {
            return
        }

        Log.d("NewIncomeVM","new Date $value and parsed to $v")
        _date = value
        notifyPropertyChanged(BR.date)
    }

    @Bindable
    fun getTag(): Tag? {
        return _tag
    }

    fun setTag(value: Tag?) {
        if (value == _tag) {
            return
        }

        _tag = value
        notifyPropertyChanged(BR.tag)
    }

    //Methods
    fun insert() {
        viewModelScope.launch {
            val amount = _amount.replace(",", ".").toDoubleOrNull()
            if (amount != null && amount > 0.0 && _description.isNotBlank() && _tag != null) {
                val newAmount = updateToDefaultCurrency(amount)

                Log.d("NewIncomeVM","Date to send with req $_date")
                val income = RemoteIncome(0,newAmount,_description,_tag?.name?: "",null,_date.toString())

                val result = incomeRepository.insert(income)
                val succeeded = result is NetworkResult.Success
                if (succeeded) {
                    val activity = saveAppApplication.getCurrentActivity() as MainActivity
                    activity.goBack()
                    Snackbar.make(
                        activity.findViewById(R.id.containerView),
                        R.string.movement_created,
                        Snackbar.LENGTH_SHORT
                    ).setAnchorView(activity.findViewById(R.id.bottomAppBar)).show()
                }
            } else {
                onAmountChanged.invoke()
                onDescriptionChanged.invoke()
                validateTag.invoke()
            }
        }
    }

    private fun updateToDefaultCurrency(amount: Double): Double {
        if (_currency.id == baseCurrency.id || CurrencyUtil.rates.size < currencies.value!!.size) {
            return amount
        }

        return amount * CurrencyUtil.rates[baseCurrency.id] / CurrencyUtil.rates[_currency.id]
    }

    fun amountOnFocusChange(v: View, hasFocus: Boolean) {
        if (!hasFocus) {
            val amount = _amount.replace(",", ".").toDoubleOrNull()
            setAmount(
                if (amount == null) ""
                else String.format("%.2f", amount)
            )
        }
    }

    private fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    // Overrides
    override fun addOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback?
    ) {
        callbacks.remove(callback)
    }
}