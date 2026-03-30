package com.subnix.app.repository

import androidx.lifecycle.LiveData
import com.subnix.app.data.Subscription
import com.subnix.app.data.SubscriptionDao

class SubscriptionRepository(private val subscriptionDao: SubscriptionDao) {
    val allSubscriptions: LiveData<List<Subscription>> = subscriptionDao.getAllSubscriptions()

    suspend fun insert(subscription: Subscription) = subscriptionDao.insert(subscription)

    suspend fun update(subscription: Subscription) = subscriptionDao.update(subscription)

    suspend fun delete(subscription: Subscription) = subscriptionDao.delete(subscription)

    suspend fun getForgottenSubscriptions(threshold: Long): List<Subscription> {
        return subscriptionDao.getForgottenSubscriptions(threshold)
    }

    suspend fun updateLastUsed(packageName: String, lastUsed: Long) {
        subscriptionDao.updateLastUsed(packageName, lastUsed)
    }

    suspend fun getAllSubscriptionsSnapshot(): List<Subscription> {
        return subscriptionDao.getAllSubscriptionsSnapshot()
    }
}
