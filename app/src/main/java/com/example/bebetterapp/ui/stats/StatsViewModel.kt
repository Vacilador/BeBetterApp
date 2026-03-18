package com.example.bebetterapp.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bebetterapp.data.repo.HabitRepository
import com.example.bebetterapp.domain.model.HabitStat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatsViewModel(
    private val repo: HabitRepository
) : ViewModel() {

    enum class StatsRange {
        DAYS_7,
        DAYS_30,
        YEAR,
        ALL
    }

    private val _stats = MutableStateFlow<List<HabitStat>>(emptyList())
    val stats: StateFlow<List<HabitStat>> = _stats.asStateFlow()

    private val _selectedRange = MutableStateFlow(StatsRange.DAYS_7)
    val selectedRange: StateFlow<StatsRange> = _selectedRange.asStateFlow()

    fun loadStats(range: StatsRange = _selectedRange.value) {
        _selectedRange.value = range

        viewModelScope.launch {
            val end = LocalDate.now()

            val start = when (range) {
                StatsRange.DAYS_7 -> end.minusDays(6)
                StatsRange.DAYS_30 -> end.minusDays(29)
                StatsRange.YEAR -> end.minusDays(364)
                StatsRange.ALL -> repo.getFirstEntryDate() ?: end
            }

            _stats.value = repo.getStats(start, end)
        }
    }
}