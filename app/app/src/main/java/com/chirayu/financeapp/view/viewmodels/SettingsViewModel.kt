package com.chirayu.financeapp.view.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chirayu.financeapp.MainActivity
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.util.SettingsUtil
import kotlinx.coroutines.flow.Flow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    val currencies: MutableLiveData<Array<Currencies>> =
        MutableLiveData<Array<Currencies>>(Currencies.values())

    val defaultCurrencyId get() : Flow<Int> = SettingsUtil.getCurrency()

    val isUpdatingCurrency: LiveData<Boolean> =
        ((application as SaveAppApplication).getCurrentActivity() as MainActivity).isUpdatingCurrencies
}
