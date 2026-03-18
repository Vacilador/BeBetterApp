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

    private val _stats = MutableStateFlow<List<HabitStat>>(emptyList())
    val stats: StateFlow<List<HabitStat>> = _stats.asStateFlow()

    fun loadLast7Days() {
        viewModelScope.launch {
            val end = LocalDate.now()
            val start = end.minusDays(6)
            _stats.value = repo.getStats(start, end)
        }
    }
}