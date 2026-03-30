package com.subnix.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.subnix.app.data.SubnixDatabase
import com.subnix.app.data.Subscription
import com.subnix.app.repository.SubscriptionRepository
import kotlinx.coroutines.launch

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SubscriptionRepository
    val allSubscriptions: LiveData<List<Subscription>>

    init {
        val dao = SubnixDatabase.getDatabase(application).subscriptionDao()
        repository = SubscriptionRepository(dao)
        allSubscriptions = repository.allSubscriptions
    }

    fun insert(subscription: Subscription) = viewModelScope.launch {
        repository.insert(subscription)
    }

    fun update(subscription: Subscription) = viewModelScope.launch {
        repository.update(subscription)
    }

    fun delete(subscription: Subscription) = viewModelScope.launch {
        repository.delete(subscription)
    }

    fun updateLastUsed(packageName: String, lastUsed: Long) = viewModelScope.launch {
        repository.updateLastUsed(packageName, lastUsed)
    }
}
