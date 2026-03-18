package com.example.bebetterapp.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    LaunchedEffect(Unit) {
        vm.loadStats()
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stats) { item ->
                    StatRow(item)
                }
            }
        }
    }
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
private fun StatsChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
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
            Column {
                Text(
                    text = stat.titleRu,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("${stat.checkedDays} / ${stat.totalDays} дней")
            }

            Text(
                text = "${stat.percent}%",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}