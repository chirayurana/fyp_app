package com.chirayu.financeapp.data.repository

import androidx.annotation.WorkerThread
import com.chirayu.financeapp.data.dao.SubscriptionDao
import com.chirayu.financeapp.model.entities.Subscription
import com.chirayu.financeapp.model.taggeditems.TaggedSubscription
import kotlinx.coroutines.flow.Flow

class SubscriptionRepository(private val subscriptionDao: SubscriptionDao) {
    val allTaggedSubscriptions: Flow<List<TaggedSubscription>> = subscriptionDao.getAllTagged()

    @WorkerThread
    suspend fun getAll(): List<Subscription> {
        return subscriptionDao.getAll()
    }

    @WorkerThread
    suspend fun getById(id: Int): Subscription? {
        return subscriptionDao.getById(id)
    }

    @WorkerThread
    suspend fun insert(subscription: Subscription) {
        subscriptionDao.insert(subscription)
    }

    @WorkerThread
    suspend fun update(subscription: Subscription) {
        subscriptionDao.update(subscription)
    }

    @WorkerThread
    suspend fun delete(subscription: Subscription) {
        subscriptionDao.delete(subscription)
    }
}
