package com.example.bebetterapp.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bebetterapp.domain.model.HabitStat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    vm: StatsViewModel,
    onBack: () -> Unit = {}
) {
    val stats by vm.stats.collectAsState()
    val selectedRange by vm.selectedRange.collectAsState()

    var sortOption by remember { mutableStateOf(StatsSortOption.BY_PERCENT) }

    LaunchedEffect(Unit) {
        vm.loadStats()
    }

    val sortedStats = remember(stats, sortOption) {
        when (sortOption) {
            StatsSortOption.BY_PERCENT -> {
                stats.sortedWith(
                    compareByDescending<HabitStat> { it.percent }
                        .thenByDescending { it.checkedDays }
                        .thenBy { it.titleRu }
                )
            }

            StatsSortOption.BY_COMPLETIONS -> {
                stats.sortedWith(
                    compareByDescending<HabitStat> { it.checkedDays }
                        .thenByDescending { it.percent }
                        .thenBy { it.titleRu }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад")
            }

            StatsRangeChips(
                selectedRange = selectedRange,
                onRangeSelected = { vm.loadStats(it) }
            )

            StatsSortChips(
                selectedSort = sortOption,
                onSortSelected = { sortOption = it }
            )

            if (sortedStats.isEmpty()) {
                EmptyStatsState(
                    selectedRange = selectedRange,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sortedStats) { item ->
                        StatRow(item)
                    }
                }
            }
        }
    }
}

private enum class StatsSortOption {
    BY_PERCENT,
    BY_COMPLETIONS
}

@Composable
private fun StatsRangeChips(
    selectedRange: StatsViewModel.StatsRange,
    onRangeSelected: (StatsViewModel.StatsRange) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatsChip(
            text = "7д",
            selected = selectedRange == StatsViewModel.StatsRange.DAYS_7,
            onClick = { onRangeSelected(StatsViewModel.StatsRange.DAYS_7) }
        )

        StatsChip(
            text = "30д",
            selected = selectedRange == StatsViewModel.StatsRange.DAYS_30,
            onClick = { onRangeSelected(StatsViewModel.StatsRange.DAYS_30) }
        )

        StatsChip(
            text = "Год",
            selected = selectedRange == StatsViewModel.StatsRange.YEAR,
            onClick = { onRangeSelected(StatsViewModel.StatsRange.YEAR) }
        )

        StatsChip(
            text = "Всё",
            selected = selectedRange == StatsViewModel.StatsRange.ALL,
            onClick = { onRangeSelected(StatsViewModel.StatsRange.ALL) }
        )
    }
}

@Composable
private fun StatsSortChips(
    selectedSort: StatsSortOption,
    onSortSelected: (StatsSortOption) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Сортировка",
            style = MaterialTheme.typography.labelLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatsChip(
                text = "По %",
                selected = selectedSort == StatsSortOption.BY_PERCENT,
                onClick = { onSortSelected(StatsSortOption.BY_PERCENT) }
            )

            StatsChip(
                text = "По выполн.",
                selected = selectedSort == StatsSortOption.BY_COMPLETIONS,
                onClick = { onSortSelected(StatsSortOption.BY_COMPLETIONS) }
            )
        }
    }
}

@Composable
private fun StatsChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) }
    )
}

@Composable
private fun StatRow(stat: HabitStat) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stat.titleRu,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${stat.checkedDays} / ${stat.totalDays} дней",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "${stat.percent}%",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun EmptyStatsState(
    selectedRange: StatsViewModel.StatsRange,
    modifier: Modifier = Modifier
) {
    val periodLabel = when (selectedRange) {
        StatsViewModel.StatsRange.DAYS_7 -> "последние 7 дней"
        StatsViewModel.StatsRange.DAYS_30 -> "последние 30 дней"
        StatsViewModel.StatsRange.YEAR -> "последний год"
        StatsViewModel.StatsRange.ALL -> "всё время"
    }

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Пока нет данных",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "За период \"$periodLabel\" пока нет отметок по привычкам.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Отмечай выполнение на главном экране — и здесь появится статистика.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}