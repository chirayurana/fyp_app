package com.chirayu.financeapp.util

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.data.repository.MovementRepository
import com.chirayu.financeapp.data.repository.SubscriptionRepository
import com.chirayu.financeapp.model.CurrencyApiResponse
import com.chirayu.financeapp.model.enums.Currencies
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.repository.RemoteBudgetRepository
import com.chirayu.financeapp.network.repository.RemoteExpenseRepository
import com.chirayu.financeapp.network.repository.RemoteSubscriptionRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.time.LocalDate

private const val BASE_URL = "https://api.frankfurter.app"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface CurrencyApiServer {
    @GET("latest")
    suspend fun getRates(): CurrencyApiResponse
}

object CurrencyUtil {
    private val retrofitService: CurrencyApiServer by lazy {
        retrofit.create(CurrencyApiServer::class.java)
    }

    private lateinit var movementRepository: RemoteExpenseRepository
    private lateinit var subscriptionRepository: RemoteSubscriptionRepository
    private lateinit var budgetRepository: RemoteBudgetRepository

    private lateinit var ratesStore: DataStore<Preferences>

    private var keys: Array<Preferences.Key<Double>> = arrayOf()

    private var DATE_KEY = stringPreferencesKey("last_update")

    var rates: Array<Double> = arrayOf()

    fun setStore(application: SaveAppApplication) {
        ratesStore = application.ratesStore
        movementRepository = application.movementRepository
        subscriptionRepository = application.subscriptionRepository
        budgetRepository = application.remoteBudgetRepository
    }

    suspend fun init() {
        keys = Currencies.values().map {
            doublePreferencesKey(it.name)
        }.toTypedArray()

        val oldDate = getDate()

        if (oldDate == null || oldDate.isBefore(LocalDate.now())) {
            try {
                val result = retrofitService.getRates()
                rates = Currencies.values().map { result.rates[it.name] ?: 1.0 }.toTypedArray()

                setDate(result.date)
                setRates(rates)
            } catch (e: Exception) {
                Log.e("API", e.message ?: "")
                rates = getRates()
            }
        } else {
            rates = getRates()
        }
    }

    suspend fun updateAllToNewCurrency(context: Context, newCurrency: Currencies) {
        val oldCurrency = SettingsUtil.getCurrency().first()
        if (newCurrency.id != oldCurrency) {
            val rate = rates[newCurrency.id] / rates[oldCurrency]
            SettingsUtil.setCurrency(newCurrency)

            val allMovements = movementRepository.getAll()
            if(allMovements is NetworkResult.Success) {
                allMovements.data.forEach {
                    val movement = it.copy(amount = it.amount * rate)
                    movementRepository.update(movement)
                }
            }
            val allSubscriptions = subscriptionRepository.getAll()
            if(allSubscriptions is NetworkResult.Success){
                allSubscriptions.data.forEach {
                    subscriptionRepository.update(it.copy(amount = it.amount * rate))
                }
            }

            val allBudgets = budgetRepository.getAll()
            if(allBudgets is NetworkResult.Success) {
                allBudgets.data.forEach {
                    val budget = it.copy(maxLimit = it.maxLimit*rate, currentSpent = it.currentSpent!!*rate)
                    budgetRepository.update(budget)
                }
            }

            StatsUtil.applyRateToAll(context, rate)
        }
    }

    private suspend fun setRates(rates: Array<Double>) {
        for (i: Int in rates.indices) {
            ratesStore.edit { pref ->
                pref[keys[i]] = rates[i]
            }
        }
    }

    private suspend fun getRates(): Array<Double> {
        return keys.map {
            ratesStore.data.map { preferences ->
                preferences[it] ?: 0.0
            }.first()
        }.toTypedArray()
    }

    private suspend fun setDate(date: String) {
        ratesStore.edit { pref ->
            pref[DATE_KEY] = date
        }
    }

    private suspend fun getDate(): LocalDate? {
        val dateStr = ratesStore.data.map { preferences ->
            preferences[DATE_KEY]
        }.first()

        return if (dateStr == null) null else LocalDate.parse(dateStr)
    }
}
