package com.example.bebetterapp.ui.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bebetterapp.domain.model.CalendarDayHighlight
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    vm: CalendarViewModel,
    onBack: () -> Unit = {}
) {
    val currentMonth by vm.currentMonth.collectAsState()
    val selectedDate by vm.selectedDate.collectAsState()
    val dayHighlights by vm.dayHighlights.collectAsState()

    val monthTitle = remember(currentMonth) {
        val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))
        currentMonth.atDay(1)
            .format(formatter)
            .replaceFirstChar { it.uppercase() }
    }

    val weeks = remember(currentMonth) {
        buildMonthGrid(currentMonth)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Календарь") }
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

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = vm::previousMonth) {
                            Text("◀")
                        }

                        Text(
                            text = monthTitle,
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextButton(onClick = vm::nextMonth) {
                            Text("▶")
                        }
                    }

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val cellSpacing = 8.dp
                        val totalSpacing = cellSpacing * 6
                        val cellWidth = (maxWidth - totalSpacing) / 7

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WeekDaysHeader(cellWidth = cellWidth)

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(weeks) { week ->
                                    WeekRow(
                                        days = week,
                                        selectedDate = selectedDate,
                                        dayHighlights = dayHighlights,
                                        cellWidth = cellWidth,
                                        onDateClick = vm::selectDate
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Выбранная дата",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = formatFullDate(selectedDate),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    TextButton(onClick = vm::goToToday) {
                        Text("Перейти к сегодня")
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekDaysHeader(cellWidth: androidx.compose.ui.unit.Dp) {
    val labels = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEach { label ->
            Box(
                modifier = Modifier
                    .width(cellWidth)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun WeekRow(
    days: List<LocalDate?>,
    selectedDate: LocalDate,
    dayHighlights: Map<LocalDate, CalendarDayHighlight>,
    cellWidth: androidx.compose.ui.unit.Dp,
    onDateClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { date ->
            CalendarDayCell(
                date = date,
                highlight = if (date != null) {
                    dayHighlights[date] ?: CalendarDayHighlight.NONE
                } else {
                    CalendarDayHighlight.NONE
                },
                isSelected = date == selectedDate,
                cellWidth = cellWidth,
                onClick = {
                    if (date != null) onDateClick(date)
                }
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate?,
    highlight: CalendarDayHighlight,
    isSelected: Boolean,
    cellWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val shape = MaterialTheme.shapes.medium

    val backgroundColor = when {
        date == null -> MaterialTheme.colorScheme.surface
        highlight == CalendarDayHighlight.RED -> Color(0xFFFFCDD2)
        highlight == CalendarDayHighlight.YELLOW -> Color(0xFFFFF9C4)
        highlight == CalendarDayHighlight.GREEN -> Color(0xFFC8E6C9)
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        date == null -> MaterialTheme.colorScheme.surface
        highlight == CalendarDayHighlight.RED -> Color(0xFF7F1D1D)
        highlight == CalendarDayHighlight.YELLOW -> Color(0xFF7A5D00)
        highlight == CalendarDayHighlight.GREEN -> Color(0xFF1B5E20)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        highlight == CalendarDayHighlight.NONE && date != null -> MaterialTheme.colorScheme.outlineVariant
        else -> Color.Transparent
    }

    Surface(
        modifier = Modifier
            .width(cellWidth)
            .aspectRatio(1f)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(enabled = date != null, onClick = onClick),
        color = backgroundColor,
        shape = shape
    ) {
        Box(
            modifier = Modifier.sizeIn(minWidth = 40.dp, minHeight = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date?.dayOfMonth?.toString().orEmpty(),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun buildMonthGrid(month: YearMonth): List<List<LocalDate?>> {
    val firstDay = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()

    val leadingEmpty = when (firstDay.dayOfWeek) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }

    val cells = mutableListOf<LocalDate?>()

    repeat(leadingEmpty) {
        cells += null
    }

    for (day in 1..daysInMonth) {
        cells += month.atDay(day)
    }

    while (cells.size % 7 != 0) {
        cells += null
    }

    return cells.chunked(7)
}

private fun formatFullDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", Locale("ru"))
    return date.format(formatter).replaceFirstChar { it.uppercase() }
}