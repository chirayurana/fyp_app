package com.chirayu.financeapp

import android.app.Activity
import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.chirayu.financeapp.data.AppDatabase
import com.chirayu.financeapp.data.repository.BudgetRepository
import com.chirayu.financeapp.data.repository.MovementRepository
import com.chirayu.financeapp.data.repository.SubscriptionRepository
import com.chirayu.financeapp.data.repository.TagRepository
import com.chirayu.financeapp.network.AuthInterceptor
import com.chirayu.financeapp.network.BackendAPI
import com.chirayu.financeapp.network.repository.RemoteBudgetRepository
import com.chirayu.financeapp.network.repository.UserRepository
import com.chirayu.financeapp.util.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SaveAppApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppDatabase.getInstance(this, applicationScope) }

    companion object {
        private const val API_BASE_URL = "http://192.168.100.80:8000/api/"
    }

    val sharedPreferencesManager by lazy { SharedPreferencesManager(this) }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sharedPreferencesManager))
            .build()
    }

    val api: BackendAPI by lazy {   Retrofit.Builder().baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(BackendAPI::class.java) }

    val movementRepository by lazy { MovementRepository(database.movementDao()) }

    val budgetRepository by lazy { BudgetRepository(database.budgetDao()) }

    val subscriptionRepository by lazy { SubscriptionRepository(database.subscriptionDao()) }

    val tagRepository by lazy { TagRepository(database.tagDao()) }

    val userRepository by lazy { UserRepository(api) }

    val remoteBudgetRepository by lazy { RemoteBudgetRepository(api) }

    val ratesStore: DataStore<Preferences> by preferencesDataStore("currencies")

    val settingsStore: DataStore<Preferences> by preferencesDataStore("settings")

    private var currentActivity: Activity? = null

    fun getCurrentActivity(): Activity? {
        return currentActivity
    }

    fun setCurrentActivity(activity: Activity?) {
        currentActivity = activity
    }
}
