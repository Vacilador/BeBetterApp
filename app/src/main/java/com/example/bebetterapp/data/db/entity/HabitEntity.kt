package com.example.bebetterapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bebetterapp.domain.model.HabitType

@Entity(
    tableName = "habits",
    indices = [Index(value = ["key"], unique = true)]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val key: String,          // HabitKey.name
    val titleRu: String,
    val type: HabitType,
    val isActive: Boolean = true
)