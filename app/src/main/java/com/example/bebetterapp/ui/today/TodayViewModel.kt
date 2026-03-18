package com.example.bebetterapp.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bebetterapp.data.repo.HabitRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodayViewModel(
    private val repo: HabitRepository
) : ViewModel() {

    private val today = LocalDate.now()

    private val selectedDate = MutableStateFlow(LocalDate.now())
    val date = selectedDate.asStateFlow()

    val habits =
        selectedDate
            .flatMapLatest { d -> repo.observeHabitsForDate(d) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val isEditable: StateFlow<Boolean> =
        date
            .map { it == today }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val canGoNext: StateFlow<Boolean> =
        date
            .map { it.isBefore(today) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch {
            repo.seedIfEmpty()
            repo.ensureDay(selectedDate.value)
        }
    }

    fun seed() {
        viewModelScope.launch { repo.seedIfEmpty() }
    }

    fun onToggle(habitId: Long, newValue: Boolean) {
        viewModelScope.launch { repo.setChecked(selectedDate.value, habitId, newValue) }
    }

    fun prevDay() {
        selectedDate.update { it.minusDays(1) }
        viewModelScope.launch { repo.ensureDay(selectedDate.value) }
    }

    fun nextDay() {
        if (selectedDate.value.isBefore(today)) {
            selectedDate.update { it.plusDays(1) }
            viewModelScope.launch { repo.ensureDay(selectedDate.value) }
        }
    }
}