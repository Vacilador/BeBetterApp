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
import com.example.bebetterapp.ui.calendar.CalendarScreen
import com.example.bebetterapp.ui.calendar.CalendarViewModel
import com.example.bebetterapp.ui.stats.StatsScreen
import com.example.bebetterapp.ui.stats.StatsViewModel
import com.example.bebetterapp.ui.today.TodayScreen
import com.example.bebetterapp.ui.today.TodayViewModel

class MainActivity : ComponentActivity() {

    private enum class AppScreen {
        TODAY,
        STATS,
        CALENDAR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as BeBetterApp
        val todayVm = TodayViewModel(app.repo)
        val statsVm = StatsViewModel(app.repo)
        val calendarVm = CalendarViewModel()

        setContent {
            MaterialTheme {
                Surface {
                    var currentScreen by rememberSaveable {
                        mutableStateOf(AppScreen.TODAY)
                    }

                    when (currentScreen) {
                        AppScreen.TODAY -> {
                            TodayScreen(
                                vm = todayVm,
                                onOpenStats = { currentScreen = AppScreen.STATS },
                                onOpenCalendar = { currentScreen = AppScreen.CALENDAR }
                            )
                        }

                        AppScreen.STATS -> {
                            StatsScreen(
                                vm = statsVm,
                                onBack = { currentScreen = AppScreen.TODAY }
                            )
                        }

                        AppScreen.CALENDAR -> {
                            CalendarScreen(
                                vm = calendarVm,
                                onBack = { currentScreen = AppScreen.TODAY }
                            )
                        }
                    }
                }
            }
        }
    }
}