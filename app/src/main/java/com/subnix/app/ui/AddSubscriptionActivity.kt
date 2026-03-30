package com.subnix.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.subnix.app.R
import com.subnix.app.data.Subscription
import com.subnix.app.util.UsageStatsHelper
import com.subnix.app.viewmodel.SubscriptionViewModel

class AddSubscriptionActivity : AppCompatActivity() {

    private val viewModel: SubscriptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subscription)

        val etAppName = findViewById<TextInputEditText>(R.id.etAppName)
        val etPackageName = findViewById<TextInputEditText>(R.id.etPackageName)
        val etMonthlyCost = findViewById<TextInputEditText>(R.id.etMonthlyCost)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)
        val btnCancel = findViewById<MaterialButton>(R.id.btnCancel)

        btnCancel.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val appName = etAppName.text?.toString()?.trim().orEmpty()
            val packageName = etPackageName.text?.toString()?.trim().orEmpty()
            val monthlyCost = etMonthlyCost.text?.toString()?.toDoubleOrNull() ?: 0.0

            if (appName.isBlank()) {
                etAppName.error = getString(R.string.app_name_label) + " is required"
                return@setOnClickListener
            }

            val lastUsed = if (packageName.isNotBlank() && UsageStatsHelper.hasUsageStatsPermission(this)) {
                UsageStatsHelper.getLastUsedForPackage(this, packageName)
            } else {
                0L
            }

            val subscription = Subscription(
                appName = appName,
                packageName = packageName,
                monthlyCost = monthlyCost,
                lastUsed = lastUsed,
                isActive = true
            )

            viewModel.insert(subscription)
            Toast.makeText(this, "Subscription saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
