package com.subnix.app.util

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process

object UsageStatsHelper {

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getLastUsedForPackage(context: Context, packageName: String): Long {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        val start = end - (1000L * 60L * 60L * 24L * 365L)
        val stats = usageStatsManager.queryAndAggregateUsageStats(start, end)
        return stats[packageName]?.lastTimeUsed ?: 0L
    }
}
