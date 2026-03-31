package com.example.bebetterapp.domain.model

data class CalendarDayDetails(
    val highlight: CalendarDayHighlight,
    val checkedHabitTitles: List<String>
)