package com.example.bebetterapp

import android.app.Application
import androidx.room.Room
import com.example.bebetterapp.data.db.AppDatabase
import com.example.bebetterapp.data.repo.HabitRepository
import com.example.bebetterapp.ui.notify.ReminderScheduler


class BeBetterApp : Application() {

    lateinit var db: AppDatabase
        private set

    lateinit var repo: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        ReminderScheduler.scheduleDaily2130(this)
        ReminderScheduler.runNow(this) //временно для проверки
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bebetter.db"
        ).build()

        repo = HabitRepository(db.habitDao(), db.dayEntryDao())
    }
}