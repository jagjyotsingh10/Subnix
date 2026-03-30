package com.subnix.app.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.subnix.app.R
import com.subnix.app.adapter.SubscriptionAdapter
import com.subnix.app.data.Subscription
import com.subnix.app.util.DateUtils
import com.subnix.app.util.NotificationHelper
import com.subnix.app.util.PaywallManager
import com.subnix.app.util.UsageStatsHelper
import com.subnix.app.viewmodel.SubscriptionViewModel
import com.subnix.app.worker.UsageStatsSyncScheduler
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val viewModel: SubscriptionViewModel by viewModels()

    private lateinit var adapter: SubscriptionAdapter
    private lateinit var tvTotalSubs: TextView
    private lateinit var tvForgotten: TextView
    private lateinit var tvMonthlyCost: TextView
    private lateinit var tvPaywall: TextView

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(this, "Notifications disabled for reminders", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTotalSubs = findViewById(R.id.tvTotalSubs)
        tvForgotten = findViewById(R.id.tvForgotten)
        tvMonthlyCost = findViewById(R.id.tvMonthlyCost)
        tvPaywall = findViewById(R.id.tvPaywall)

        setupRecyclerView()
        setupFab()
        observeData()

        ensureUsageStatsPermission()
        ensureNotificationPermission()

        NotificationHelper.ensureChannel(this)
        UsageStatsSyncScheduler.scheduleDailySync(this)
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvSubscriptions)
        adapter = SubscriptionAdapter(
            onClick = { subscription -> toggleActive(subscription) },
            onLongClick = { subscription -> confirmDelete(subscription) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddSubscriptionActivity::class.java))
        }
    }

    private fun observeData() {
        viewModel.allSubscriptions.observe(this) { subscriptions ->
            adapter.submitList(subscriptions)
            renderSummary(subscriptions)
            refreshLastUsedFromUsageStats(subscriptions)
        }
    }

    private fun renderSummary(subscriptions: List<Subscription>) {
        val total = subscriptions.size
        val forgotten = subscriptions.count { DateUtils.computeStatus(it.lastUsed).name == "FORGOTTEN" && it.isActive }
        val monthly = subscriptions.filter { it.isActive }.sumOf { it.monthlyCost }

        tvTotalSubs.text = total.toString()
        tvForgotten.text = forgotten.toString()
        tvMonthlyCost.text = String.format(Locale.CANADA, "CAD $%.2f", monthly)
        tvPaywall.visibility = if (PaywallManager.shouldShowProPaywall(this)) TextView.VISIBLE else TextView.GONE
    }

    private fun toggleActive(subscription: Subscription) {
        val updated = subscription.copy(isActive = !subscription.isActive)
        viewModel.update(updated)
    }

    private fun confirmDelete(subscription: Subscription) {
        AlertDialog.Builder(this)
            .setTitle("Delete subscription")
            .setMessage("Remove ${subscription.appName} from your list?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete(subscription)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun ensureUsageStatsPermission() {
        if (!UsageStatsHelper.hasUsageStatsPermission(this)) {
            AlertDialog.Builder(this)
                .setTitle("Usage access required")
                .setMessage(getString(R.string.permission_required))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
                .show()
        }
    }

    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun refreshLastUsedFromUsageStats(subscriptions: List<Subscription>) {
        if (!UsageStatsHelper.hasUsageStatsPermission(this)) return

        subscriptions.forEach { sub ->
            if (sub.packageName.isBlank()) return@forEach
            val usageLastUsed = UsageStatsHelper.getLastUsedForPackage(this, sub.packageName)
            if (usageLastUsed > 0L && usageLastUsed != sub.lastUsed) {
                viewModel.updateLastUsed(sub.packageName, usageLastUsed)
            }
        }
    }
}
