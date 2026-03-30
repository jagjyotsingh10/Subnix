package com.subnix.app.util

import java.util.concurrent.TimeUnit

object DateUtils {
    private const val DAY_MS = 24L * 60L * 60L * 1000L

    fun daysSince(timestamp: Long, now: Long = System.currentTimeMillis()): Long {
        if (timestamp <= 0L) return Long.MAX_VALUE
        return TimeUnit.MILLISECONDS.toDays((now - timestamp).coerceAtLeast(0L))
    }

    fun computeStatus(lastUsed: Long): SubscriptionStatus {
        val days = daysSince(lastUsed)
        return when {
            days < 14 -> SubscriptionStatus.ACTIVE
            days < 30 -> SubscriptionStatus.IDLE
            else -> SubscriptionStatus.FORGOTTEN
        }
    }

    fun formatLastUsed(lastUsed: Long): String {
        val days = daysSince(lastUsed)
        return if (days == Long.MAX_VALUE) {
            "Last opened: never"
        } else {
            "Last opened: $days day${if (days == 1L) "" else "s"} ago"
        }
    }

    fun forgottenThreshold(now: Long = System.currentTimeMillis()): Long {
        return now - (30L * DAY_MS)
    }
}
