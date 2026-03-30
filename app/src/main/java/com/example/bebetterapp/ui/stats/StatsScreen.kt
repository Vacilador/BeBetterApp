package com.example.bebetterapp.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bebetterapp.domain.model.DailyCompletionStat
import com.example.bebetterapp.domain.model.HabitStat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    vm: StatsViewModel,
    onBack: () -> Unit = {}
) {
    val stats by vm.stats.collectAsState()
    val dailyStats by vm.dailyStats.collectAsState()
    val selectedRange by vm.selectedRange.collectAsState()

    var sortOption by remember { mutableStateOf(StatsSortOption.BY_PERCENT) }
    var hideEmpty by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadStats()
    }

    val visibleStats = remember(stats, hideEmpty) {
        if (hideEmpty) {
            stats.filter { it.checkedDays > 0 }
        } else {
            stats
        }
    }

    val sortedStats = remember(visibleStats, sortOption) {
        when (sortOption) {
            StatsSortOption.BY_PERCENT -> {
                visibleStats.sortedWith(
                    compareByDescending<HabitStat> { it.percent }
                        .thenByDescending { it.checkedDays }
                        .thenBy { it.titleRu }
                )
            }

            StatsSortOption.BY_COMPLETIONS -> {
                visibleStats.sortedWith(
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

            HideEmptyToggle(
                hideEmpty = hideEmpty,
                onCheckedChange = { hideEmpty = it }
            )

            DailyBarChart(
                dailyStats = dailyStats,
                modifier = Modifier.fillMaxWidth()
            )

            if (sortedStats.isEmpty()) {
                EmptyStatsState(
                    selectedRange = selectedRange,
                    hideEmpty = hideEmpty,
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
private fun HideEmptyToggle(
    hideEmpty: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Скрыть пустые",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Не показывать привычки без выполнений за период",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = hideEmpty,
                onCheckedChange = onCheckedChange
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
private fun DailyBarChart(
    dailyStats: List<DailyCompletionStat>,
    modifier: Modifier = Modifier
) {
    val maxCount = dailyStats.maxOfOrNull { it.completedCount } ?: 0

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Выполнения по дням",
                style = MaterialTheme.typography.titleMedium
            )

            if (dailyStats.isEmpty()) {
                Text(
                    text = "Нет данных для графика.",
                    style = MaterialTheme.typography.bodyMedium
                )
                return@Column
            }

            Text(
                text = "Каждый столбик — количество выполненных привычек за день",
                style = MaterialTheme.typography.bodySmall
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dailyStats) { day ->
                    DayBar(
                        stat = day,
                        maxCount = maxCount
                    )
                }
            }
        }
    }
}

@Composable
private fun DayBar(
    stat: DailyCompletionStat,
    maxCount: Int
) {
    val barFraction = if (maxCount <= 0) {
        0f
    } else {
        stat.completedCount.toFloat() / maxCount.toFloat()
    }.coerceIn(0f, 1f)

    val dayFormatter = remember {
        DateTimeFormatter.ofPattern("dd", Locale.getDefault())
    }

    val monthFormatter = remember {
        DateTimeFormatter.ofPattern("MM", Locale.getDefault())
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stat.completedCount.toString(),
            style = MaterialTheme.typography.labelSmall
        )

        Box(
            modifier = Modifier
                .width(20.dp)
                .height(120.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(barFraction)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        Text(
            text = stat.date.format(dayFormatter),
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = stat.date.format(monthFormatter),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stat.titleRu,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Выполнено: ${stat.checkedDays} из ${stat.totalDays} дней",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "${stat.percent}%",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
private fun EmptyStatsState(
    selectedRange: StatsViewModel.StatsRange,
    hideEmpty: Boolean,
    modifier: Modifier = Modifier
) {
    val periodLabel = when (selectedRange) {
        StatsViewModel.StatsRange.DAYS_7 -> "последние 7 дней"
        StatsViewModel.StatsRange.DAYS_30 -> "последние 30 дней"
        StatsViewModel.StatsRange.YEAR -> "последний год"
        StatsViewModel.StatsRange.ALL -> "всё время"
    }

    val message = if (hideEmpty) {
        "После скрытия пустых привычек за период \"$periodLabel\" ничего не осталось."
    } else {
        "За период \"$periodLabel\" пока нет отметок по привычкам."
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
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Отмечай выполнение на главном экране — и здесь появится статистика.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}