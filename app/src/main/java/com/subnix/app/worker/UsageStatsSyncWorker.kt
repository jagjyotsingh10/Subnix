package com.subnix.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.subnix.app.data.SubnixDatabase
import com.subnix.app.repository.SubscriptionRepository
import com.subnix.app.util.DateUtils
import com.subnix.app.util.NotificationHelper
import com.subnix.app.util.PaywallManager
import com.subnix.app.util.UsageStatsHelper

class UsageStatsSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = SubscriptionRepository(
            SubnixDatabase.getDatabase(applicationContext).subscriptionDao()
        )

        val all = repository.getAllSubscriptionsSnapshot()
        for (subscription in all) {
            if (subscription.packageName.isNotBlank()) {
                val lastUsed = UsageStatsHelper.getLastUsedForPackage(
                    applicationContext,
                    subscription.packageName
                )
                if (lastUsed > 0L) {
                    repository.updateLastUsed(subscription.packageName, lastUsed)
                }
            }
        }

        val forgotten = repository.getForgottenSubscriptions(DateUtils.forgottenThreshold())
        forgotten.forEach { sub ->
            val days = DateUtils.daysSince(sub.lastUsed)
            NotificationHelper.sendForgottenAlert(applicationContext, sub.appName, days)
            PaywallManager.incrementForgottenAlertCount(applicationContext)
        }

        return Result.success()
    }
}
