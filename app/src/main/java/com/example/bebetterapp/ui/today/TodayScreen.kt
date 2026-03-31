package com.example.bebetterapp.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bebetterapp.domain.model.HabitUi
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    vm: TodayViewModel,
    onOpenStats: () -> Unit = {},
    onOpenCalendar: () -> Unit = {}
) {
    LaunchedEffect(Unit) { vm.seed() }

    val habits by vm.habits.collectAsState(initial = emptyList())
    val date by vm.date.collectAsState()
    val canGoNext by vm.canGoNext.collectAsState()
    val isEditable by vm.isEditable.collectAsState()

    val dateText = remember(date) {
        val ru = Locale("ru")
        val fmt = DateTimeFormatter.ofPattern("d MMMM, EEEE", ru)
        date.format(fmt).replaceFirstChar { it.uppercase() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Стань лучше") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { vm.prevDay() }) {
                    Text("◀")
                }

                Text(
                    text = dateText,
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(
                    onClick = { vm.nextDay() },
                    enabled = canGoNext
                ) {
                    Text("▶")
                }
            }

            Card {
                Column(Modifier.padding(12.dp)) {
                    HabitChecklist(
                        habits = habits,
                        isEditable = isEditable,
                        onToggle = vm::onToggle
                    )
                }
            }

            Button(
                onClick = onOpenStats,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Статистика")
            }

            Button(
                onClick = onOpenCalendar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Календарь")
            }
        }
    }
}

@Composable
private fun HabitChecklist(
    habits: List<HabitUi>,
    isEditable: Boolean,
    onToggle: (habitId: Long, newValue: Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(habits, key = { it.id }) { h ->
            HabitRow(
                habit = h,
                isEditable = isEditable,
                onToggle = onToggle
            )
        }
    }
}

@Composable
private fun HabitRow(
    habit: HabitUi,
    isEditable: Boolean,
    onToggle: (habitId: Long, newValue: Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = habit.titleRu,
                style = MaterialTheme.typography.bodyLarge
            )

            if (habit.key.trackStreak && habit.streak > 0) {
                Text(
                    text = " ${habit.streak}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Checkbox(
            checked = habit.isChecked,
            onCheckedChange = { onToggle(habit.id, it) },
            enabled = isEditable
        )
    }
}