package com.subnix.app.util

import android.content.Context

object PaywallManager {
    private const val PREFS = "subnix_prefs"
    private const val KEY_FORGOTTEN_ALERT_COUNT = "forgotten_alert_count"

    fun incrementForgottenAlertCount(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_FORGOTTEN_ALERT_COUNT, 0)
        prefs.edit().putInt(KEY_FORGOTTEN_ALERT_COUNT, current + 1).apply()
    }

    fun getForgottenAlertCount(context: Context): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_FORGOTTEN_ALERT_COUNT, 0)
    }

    fun shouldShowProPaywall(context: Context): Boolean {
        return getForgottenAlertCount(context) >= 3
    }
}
