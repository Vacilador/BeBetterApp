package com.example.bebetterapp.ui.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bebetterapp.R
import androidx.room.Room
import com.example.bebetterapp.data.db.AppDatabase
import com.example.bebetterapp.data.repo.HabitRepository
import java.time.LocalDate


class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bebetter.db"
        ).build()

        val repo = HabitRepository(db.habitDao(), db.dayEntryDao())

        val need = repo.shouldRemind(LocalDate.now())

        if (!need) {
            return Result.success() // ничего не делаем
        }

        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminders"

        val channel = NotificationChannel(
            channelId,
            "Напоминания",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        nm.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Стань лучше")
            .setContentText("Не забудь отметить галочки 🙂")
            .setAutoCancel(true)
            .build()

        nm.notify(1001, notification)
        return Result.success()
    }
}