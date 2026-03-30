package com.subnix.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String,
    val monthlyCost: Double,
    val lastUsed: Long,
    val isActive: Boolean = true
)
