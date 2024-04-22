package com.chirayu.financeapp.network.repository

import com.chirayu.financeapp.network.BackendAPI
import com.chirayu.financeapp.network.data.handleRemoteRequest
import com.chirayu.financeapp.network.models.RemoteExpense
import com.chirayu.financeapp.network.models.RemoteSubscription

class RemoteSubscriptionRepository(private val api: BackendAPI) {

    suspend fun retrieveAllSubscriptions() = handleRemoteRequest {
        api.retrieveAllSubscriptions()
    }

    suspend fun addExpense(subscription: RemoteSubscription) = handleRemoteRequest {
        api.addSubscription(subscription)
    }

    suspend fun retrieveExpense(subscriptionId : Int) = handleRemoteRequest {
        api.retrieveSubscription(subscriptionId)
    }

    suspend fun updateExpense(subscriptionId : Int, subscription: RemoteSubscription) = handleRemoteRequest {
        api.updateSubscription(subscriptionId,subscription)
    }

    suspend fun deleteExpense(subscriptionId : Int) = handleRemoteRequest {
        api.deleteSubscription(subscriptionId)
    }
}