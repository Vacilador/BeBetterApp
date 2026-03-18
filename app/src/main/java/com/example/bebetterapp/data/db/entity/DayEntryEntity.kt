package com.example.bebetterapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import java.time.LocalDate

@Entity(
    tableName = "day_entries",
    primaryKeys = ["date", "habitId"],
    indices = [Index("date"), Index("habitId")]
)
data class DayEntryEntity(
    val date: LocalDate,
    val habitId: Long,
    val value: Boolean
)