package com.example.bebetterapp.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.bebetterapp.domain.model.CalendarDayHighlight
import com.example.bebetterapp.domain.model.DayColorStat
import com.example.bebetterapp.domain.model.HabitStat
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    vm: StatsViewModel,
    onBack: () -> Unit = {}
) {
    val stats by vm.stats.collectAsState()
    val dayColorStats by vm.dayColorStats.collectAsState()
    val selectedRange by vm.selectedRange.collectAsState()

    var sortOption by remember { mutableStateOf(StatsSortOption.BY_PERCENT) }
    var hideEmpty by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadStats()
    }

    val visibleStats = remember(stats, hideEmpty) {
        if (hideEmpty) stats.filter { it.checkedDays > 0 } else stats
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

            DayQualityPieChart(
                stats = dayColorStats,
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

private data class PieSliceUi(
    val highlight: CalendarDayHighlight,
    val count: Int,
    val percent: Int,
    val startAngle: Float,
    val sweepAngle: Float
)

@Composable
private fun DayQualityPieChart(
    stats: List<DayColorStat>,
    modifier: Modifier = Modifier
) {
    val total = stats.sumOf { it.count }

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Качество дней",
                style = MaterialTheme.typography.titleMedium
            )

            if (total == 0) {
                Text(
                    text = "Нет данных для графика.",
                    style = MaterialTheme.typography.bodyMedium
                )
                return@Column
            }

            val slices = remember(stats, total) {
                buildList {
                    var startAngle = -90f
                    stats.forEach { item ->
                        val sweep = (item.count.toFloat() / total.toFloat()) * 360f
                        add(
                            PieSliceUi(
                                highlight = item.highlight,
                                count = item.count,
                                percent = ((item.count * 100f) / total).roundToInt(),
                                startAngle = startAngle,
                                sweepAngle = sweep
                            )
                        )
                        startAngle += sweep
                    }
                }
            }

            PieChartWithLabels(
                slices = slices,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stats.forEach { item ->
                    LegendRow(item = item, total = total)
                }
            }
        }
    }
}

@Composable
private fun PieChartWithLabels(
    slices: List<PieSliceUi>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 220.dp
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val boxSize = chartSize
        val center = chartSize / 2
        val labelRadius = chartSize * 0.28f

        Box(
            modifier = Modifier.size(boxSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(chartSize)
            ) {
                val strokeWidth = 56.dp.toPx()

                slices.forEach { slice ->
                    if (slice.sweepAngle > 0f) {
                        drawArc(
                            color = pieColor(slice.highlight),
                            startAngle = slice.startAngle,
                            sweepAngle = slice.sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidth),
                            size = Size(size.width, size.height)
                        )
                    }
                }
            }

            slices.forEach { slice ->
                if (slice.percent >= 8 && slice.sweepAngle > 0f) {
                    val midAngle = slice.startAngle + slice.sweepAngle / 2f
                    val radians = Math.toRadians(midAngle.toDouble())
                    val dx = (cos(radians) * labelRadius.value).toFloat()
                    val dy = (sin(radians) * labelRadius.value).toFloat()

                    Text(
                        text = "${slice.percent}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = pieTextColor(slice.highlight),
                        modifier = Modifier.offset(
                            x = dx.dp,
                            y = dy.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendRow(
    item: DayColorStat,
    total: Int
) {
    val percent = if (total == 0) 0 else ((item.count * 100f) / total).roundToInt()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(pieColor(item.highlight))
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = legendLabel(item.highlight),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "${item.count} дн. · $percent%",
            style = MaterialTheme.typography.bodySmall
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

private fun pieColor(highlight: CalendarDayHighlight): Color {
    return when (highlight) {
        CalendarDayHighlight.RED -> Color(0xFFE57373)
        CalendarDayHighlight.YELLOW -> Color(0xFFFFF176)
        CalendarDayHighlight.GREEN -> Color(0xFF81C784)
        CalendarDayHighlight.NONE -> Color(0xFFF5F5F5)
    }
}

private fun pieTextColor(highlight: CalendarDayHighlight): Color {
    return when (highlight) {
        CalendarDayHighlight.NONE -> Color(0xFF666666)
        else -> Color(0xFF222222)
    }
}

private fun legendLabel(highlight: CalendarDayHighlight): String {
    return when (highlight) {
        CalendarDayHighlight.RED -> "плохие дни"
        CalendarDayHighlight.YELLOW -> "ты можешь лучше"
        CalendarDayHighlight.GREEN -> "молодец"
        CalendarDayHighlight.NONE -> "не забывай становиться лучше"
    }
}