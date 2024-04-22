package com.chirayu.financeapp.network.repository

import com.chirayu.financeapp.network.BackendAPI
import com.chirayu.financeapp.network.data.NetworkResult
import com.chirayu.financeapp.network.data.handleRemoteRequest
import com.chirayu.financeapp.network.models.Token
import com.chirayu.financeapp.network.models.User

class UserRepository(private val api : BackendAPI) {

    suspend fun signup(user : User) : NetworkResult<User> {
        return handleRemoteRequest {
            api.signup(user)
        }
    }

    suspend fun login(user: User) : NetworkResult<Token> {
        return handleRemoteRequest {
            api.login(user)
        }
    }

    suspend fun logout() : NetworkResult<Void> {
        return handleRemoteRequest {
            api.logout()
        }
    }

}