package com.chirayu.financeapp.network

import android.util.Log
import com.chirayu.financeapp.util.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sharedPreferencesManager: SharedPreferencesManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val headers = chain.request().headers()

        if(headers["Authorization"] != null) {
            val token = sharedPreferencesManager.getPreference(
                SharedPreferencesManager.TOKEN_KEY
            )
            token?.let {
                request.header("Authorization","Token $it")
            }
            Log.d("AuthInterceptor","auth head!")
            Log.d("AuthInterceptor","token = $token")
        }

        return chain.proceed(request.build())
    }
}