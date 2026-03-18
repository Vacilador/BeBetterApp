package com.example.bebetterapp.domain.model

data class HabitStat(
    val habitId: Long,
    val key: HabitKey,
    val titleRu: String,
    val type: HabitType,
    val checkedDays: Int,
    val totalDays: Int
) {
    val percent: Int = if (totalDays == 0) 0 else ((checkedDays * 100.0) / totalDays).toInt()
}
