package com.chirayu.financeapp.util

import android.content.Context
import android.util.Log

class SharedPreferencesManager(private val context : Context) {

    private val KEY = "PREFERENCES_KEY"

    companion object {
        const val TOKEN_KEY = "TOKEN"
    }

    fun addPreference(key : String, value : String) {
        val pref = context.getSharedPreferences(KEY,Context.MODE_PRIVATE)

        with(pref.edit()){
            putString(key,value)
            apply()
        }
    }

    fun getPreference(key : String) : String? {
        val pref = context.getSharedPreferences(KEY,Context.MODE_PRIVATE)

        return pref.getString(key,null)
    }

    fun removePreference(key: String) {
        val pref = context.getSharedPreferences(KEY,Context.MODE_PRIVATE)

        with(pref.edit()){
            remove(key)
            apply()
        }
    }
}