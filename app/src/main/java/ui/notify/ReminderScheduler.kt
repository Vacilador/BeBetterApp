package com.example.bebetterapp.ui.notify

import android.content.Context
import androidx.work.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    fun runNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    fun scheduleDaily2130(context: Context) {
        val now = LocalDateTime.now()
        val target = now.toLocalDate().atTime(LocalTime.of(21, 30))
        val firstRun = if (now.isBefore(target)) target else target.plusDays(1)

        val initialDelay = Duration.between(now, firstRun).toMillis()

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder_2130",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}