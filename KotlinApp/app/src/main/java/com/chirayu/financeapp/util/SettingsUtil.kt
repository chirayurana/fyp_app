package com.chirayu.financeapp.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.chirayu.financeapp.SaveAppApplication
import com.chirayu.financeapp.model.enums.Currencies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

object SettingsUtil {
    private var settingsStore: DataStore<Preferences>? = null

    private val DEFAULT_CURRENCY = intPreferencesKey("default_currency")

    fun setStore(application: SaveAppApplication) {
        settingsStore = application.settingsStore
    }

    suspend fun setCurrency(newCurrency: Currencies) {
        settingsStore!!.edit { pref ->
            pref[DEFAULT_CURRENCY] = newCurrency.id
        }
    }

    fun getCurrency(): Flow<Int> {
        return settingsStore!!.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[DEFAULT_CURRENCY] ?: 0
            }
    }
}
