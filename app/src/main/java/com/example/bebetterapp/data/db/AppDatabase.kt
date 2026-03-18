package com.example.bebetterapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bebetterapp.data.db.dao.DayEntryDao
import com.example.bebetterapp.data.db.dao.HabitDao
import com.example.bebetterapp.data.db.entity.DayEntryEntity
import com.example.bebetterapp.data.db.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, DayEntryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun dayEntryDao(): DayEntryDao
}