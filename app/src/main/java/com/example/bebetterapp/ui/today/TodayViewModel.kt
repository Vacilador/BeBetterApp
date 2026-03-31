package com.example.bebetterapp.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bebetterapp.data.repo.HabitRepository
import com.example.bebetterapp.domain.model.HabitUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TodayViewModel(
    private val repo: HabitRepository
) : ViewModel() {

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date.asStateFlow()

    val habits = _date.flatMapLatest { date ->
        repo.observeHabitsForDate(date)
    }

    private val _canGoNext = MutableStateFlow(false)
    val canGoNext: StateFlow<Boolean> = _canGoNext.asStateFlow()

    private val _isEditable = MutableStateFlow(true)
    val isEditable: StateFlow<Boolean> = _isEditable.asStateFlow()

    fun seed() {
        viewModelScope.launch {
            repo.seedIfEmpty()
            syncDateState()
        }
    }

    fun prevDay() {
        _date.value = _date.value.minusDays(1)
        viewModelScope.launch {
            syncDateState()
        }
    }

    fun nextDay() {
        if (!_canGoNext.value) return
        _date.value = _date.value.plusDays(1)
        viewModelScope.launch {
            syncDateState()
        }
    }

    fun openDate(date: LocalDate) {
        _date.value = date
        viewModelScope.launch {
            syncDateState()
        }
    }

    fun onToggle(habitId: Long, newValue: Boolean) {
        viewModelScope.launch {
            repo.setChecked(_date.value, habitId, newValue)
        }
    }

    private suspend fun syncDateState() {
        repo.ensureDay(_date.value)

        val today = LocalDate.now()
        _canGoNext.value = _date.value.isBefore(today)
        _isEditable.value = !_date.value.isAfter(today)
    }
}