package com.chirayu.financeapp.network.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response


suspend fun <T> handleRemoteRequest(
    execute: suspend () -> Response<T>
): NetworkResult<T> {
    return try {
        withContext(Dispatchers.IO) {
            val response = execute()
            val body = response.body()
            Log.d("HandleRemoteRequest","response code ${response.code()}")
            if (response.isSuccessful && body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        }
    } catch (e: HttpException) {
        NetworkResult.Error(e.code(), e.message())
    } catch (e: Throwable) {
        NetworkResult.Exception(e)
    }
}


