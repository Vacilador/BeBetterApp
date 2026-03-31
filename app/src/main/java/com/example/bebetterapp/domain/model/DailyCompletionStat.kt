package com.example.bebetterapp.domain.model

import java.time.LocalDate

data class DailyCompletionStat(
    val date: LocalDate,
    val completedCount: Int
)