package com.example.bebetterapp.domain.model

data class HabitUi(
    val id: Long,
    val key: HabitKey,
    val titleRu: String,
    val type: HabitType,
    val isChecked: Boolean,
    val streak: Int = 0
)