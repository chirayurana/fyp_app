package com.chirayu.financeapp.network.data

sealed interface NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class Error<T>(val code: Int, val message: String?) : NetworkResult<T>
    data class Exception<T>(val e: Throwable) : NetworkResult<T>
}

