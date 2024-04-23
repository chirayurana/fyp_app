package com.chirayu.financeapp.network.repository

import com.chirayu.financeapp.network.BackendAPI
import com.chirayu.financeapp.network.data.handleRemoteRequest
import com.chirayu.financeapp.network.models.RemoteExpense
import com.chirayu.financeapp.network.models.RemoteSubscription

class RemoteSubscriptionRepository(private val api: BackendAPI) {

    suspend fun getAll() = handleRemoteRequest {
        api.retrieveAllSubscriptions()
    }

    suspend fun insert(subscription: RemoteSubscription) = handleRemoteRequest {
        api.addSubscription(subscription)
    }

    suspend fun getById(subscriptionId : Int) = handleRemoteRequest {
        api.retrieveSubscription(subscriptionId)
    }

    suspend fun update(subscription: RemoteSubscription) = handleRemoteRequest {
        api.updateSubscription(subscription.id?: 0,subscription)
    }

    suspend fun delete(subscription: RemoteSubscription) = handleRemoteRequest {
        api.deleteSubscription(subscription.id?: 0)
    }
}