package com.subnix.app

import android.app.Application
import com.subnix.app.worker.UsageStatsSyncScheduler

class SubnixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UsageStatsSyncScheduler.scheduleDailySync(this)
    }
}
