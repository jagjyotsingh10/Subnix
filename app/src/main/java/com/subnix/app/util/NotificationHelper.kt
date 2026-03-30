package com.subnix.app.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.subnix.app.R
import kotlin.random.Random

object NotificationHelper {
    const val CHANNEL_ID = "forgotten_subscriptions"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Forgotten Subscriptions",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Alerts when a subscription app has not been opened recently."
            manager.createNotificationChannel(channel)
        }
    }

    fun canPostNotifications(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendForgottenAlert(context: Context, appName: String, daysAgo: Long) {
        if (!canPostNotifications(context)) return
        ensureChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Subnix reminder")
            .setContentText("$appName: last opened $daysAgo days ago")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(Random.nextInt(), notification)
    }
}
