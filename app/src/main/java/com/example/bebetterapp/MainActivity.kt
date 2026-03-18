package com.example.bebetterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.bebetterapp.ui.stats.StatsScreen
import com.example.bebetterapp.ui.stats.StatsViewModel
import com.example.bebetterapp.ui.today.TodayScreen
import com.example.bebetterapp.ui.today.TodayViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as BeBetterApp
        val todayVm = TodayViewModel(app.repo)
        val statsVm = StatsViewModel(app.repo)

        setContent {
            MaterialTheme {
                Surface {
                    var showStats by rememberSaveable { mutableStateOf(false) }

                    if (showStats) {
                        StatsScreen(
                            vm = statsVm,
                            onBack = { showStats = false }
                        )
                    } else {
                        TodayScreen(
                            vm = todayVm,
                            onOpenStats = { showStats = true }
                        )
                    }
                }
            }
        }
    }
}