package com.example.bebetterapp.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bebetterapp.data.repo.HabitRepository
import com.example.bebetterapp.domain.model.CalendarDayDetails
import com.example.bebetterapp.domain.model.CalendarDayHighlight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(
    private val repo: HabitRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _dayHighlights =
        MutableStateFlow<Map<LocalDate, CalendarDayHighlight>>(emptyMap())
    val dayHighlights: StateFlow<Map<LocalDate, CalendarDayHighlight>> =
        _dayHighlights.asStateFlow()

    private val _selectedDayDetails = MutableStateFlow(
        CalendarDayDetails(
            highlight = CalendarDayHighlight.NONE,
            checkedHabitTitles = emptyList()
        )
    )
    val selectedDayDetails: StateFlow<CalendarDayDetails> =
        _selectedDayDetails.asStateFlow()

    init {
        loadMonthHighlights()
        loadSelectedDayDetails()
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
        loadMonthHighlights()
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
        loadMonthHighlights()
    }

    fun selectDate(date: LocalDate) {
        val oldMonth = _currentMonth.value
        val newMonth = YearMonth.from(date)

        _selectedDate.value = date
        _currentMonth.value = newMonth

        if (oldMonth != newMonth) {
            loadMonthHighlights()
        }
        loadSelectedDayDetails()
    }

    fun goToToday() {
        val today = LocalDate.now()
        val oldMonth = _currentMonth.value
        val newMonth = YearMonth.from(today)

        _selectedDate.value = today
        _currentMonth.value = newMonth

        if (oldMonth != newMonth) {
            loadMonthHighlights()
        }
        loadSelectedDayDetails()
    }

    private fun loadMonthHighlights() {
        viewModelScope.launch {
            _dayHighlights.value = repo.getCalendarDayHighlights(_currentMonth.value)
        }
    }

    private fun loadSelectedDayDetails() {
        viewModelScope.launch {
            _selectedDayDetails.value = repo.getCalendarDayDetails(_selectedDate.value)
        }
    }
}