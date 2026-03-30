package com.subnix.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY appName ASC")
    fun getAllSubscriptions(): LiveData<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE lastUsed <= :threshold AND isActive = 1")
    suspend fun getForgottenSubscriptions(threshold: Long): List<Subscription>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: Subscription)

    @Update
    suspend fun update(subscription: Subscription)

    @Delete
    suspend fun delete(subscription: Subscription)

    @Query("UPDATE subscriptions SET lastUsed = :lastUsed WHERE packageName = :packageName")
    suspend fun updateLastUsed(packageName: String, lastUsed: Long)

    @Query("SELECT * FROM subscriptions")
    suspend fun getAllSubscriptionsSnapshot(): List<Subscription>
}
