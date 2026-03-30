package com.example.bebetterapp.data.repo

import com.example.bebetterapp.data.db.dao.DayEntryDao
import com.example.bebetterapp.data.db.dao.HabitDao
import com.example.bebetterapp.data.db.entity.DayEntryEntity
import com.example.bebetterapp.data.db.entity.HabitEntity
import com.example.bebetterapp.domain.model.DailyCompletionStat
import com.example.bebetterapp.domain.model.HabitKey
import com.example.bebetterapp.domain.model.HabitStat
import com.example.bebetterapp.domain.model.HabitType
import com.example.bebetterapp.domain.model.HabitUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HabitRepository(
    private val habitDao: HabitDao,
    private val dayEntryDao: DayEntryDao
) {
    suspend fun seedIfEmpty() {
        if (habitDao.countAll() > 0) return

        val seed = listOf(
            HabitEntity(
                key = HabitKey.ALCOHOL.name,
                titleRu = "Алкоголь",
                type = HabitType.NEGATIVE
            ),
            HabitEntity(
                key = HabitKey.SPORT.name,
                titleRu = "Спорт",
                type = HabitType.POSITIVE
            ),
            HabitEntity(
                key = HabitKey.SLEEP_OK.name,
                titleRu = "Сон норм",
                type = HabitType.NEUTRAL
            ),
            HabitEntity(
                key = HabitKey.SELF_DEV.name,
                titleRu = "Саморазвитие",
                type = HabitType.POSITIVE
            ),
            HabitEntity(
                key = HabitKey.FAMILY.name,
                titleRu = "Семья",
                type = HabitType.POSITIVE
            ),
            HabitEntity(
                key = HabitKey.FRIENDS.name,
                titleRu = "Друзья",
                type = HabitType.POSITIVE
            ),
        )

        habitDao.insertAll(seed)
    }

    fun observeHabitsForDate(date: LocalDate): Flow<List<HabitUi>> =
        combine(
            habitDao.observeActiveHabits(),
            dayEntryDao.observeEntriesForDate(date)
        ) { habits, entries ->
            val map = entries.associateBy { it.habitId }

            habits.map { h ->
                HabitUi(
                    id = h.id,
                    key = HabitKey.valueOf(h.key),
                    titleRu = h.titleRu,
                    type = h.type,
                    isChecked = map[h.id]?.value ?: false,
                    streak = 0
                )
            }
        }.flatMapLatest { list ->
            kotlinx.coroutines.flow.flow {
                val withStreaks = list.map { ui ->
                    val streak = getStreak(ui.id, date)
                    ui.copy(streak = streak)
                }
                emit(withStreaks)
            }
        }

    suspend fun setChecked(date: LocalDate, habitId: Long, value: Boolean) {
        dayEntryDao.upsert(
            DayEntryEntity(
                date = date,
                habitId = habitId,
                value = value
            )
        )
    }

    suspend fun ensureDay(date: LocalDate) {
        val habits = habitDao.getActiveHabits()
        val existing = dayEntryDao.getEntriesForDate(date)
        val existingIds = existing.map { it.habitId }.toHashSet()

        val missing = habits
            .filter { it.id !in existingIds }
            .map { h ->
                DayEntryEntity(
                    date = date,
                    habitId = h.id,
                    value = false
                )
            }

        if (missing.isNotEmpty()) {
            dayEntryDao.insertAll(missing)
        }
    }

    suspend fun getStreak(habitId: Long, fromDate: LocalDate): Int {
        var date = fromDate
        var streak = 0

        while (true) {
            val entry = dayEntryDao.getEntry(habitId, date) ?: break
            if (!entry.value) break

            streak++
            date = date.minusDays(1)
        }

        return streak
    }

    suspend fun shouldRemind(date: LocalDate): Boolean {
        val active = habitDao.getActiveHabits()
        if (active.isEmpty()) return false

        val anyChecked = dayEntryDao.hasAnyChecked(date)
        return !anyChecked
    }

    suspend fun getStats(start: LocalDate, end: LocalDate): List<HabitStat> {
        val habits = habitDao.getActiveHabits()
        val entries = dayEntryDao.getEntriesBetween(start, end)

        val checkedByHabit = entries
            .filter { it.value }
            .groupingBy { it.habitId }
            .eachCount()

        val totalDays = ChronoUnit.DAYS.between(start, end).toInt() + 1

        return habits.map { h ->
            HabitStat(
                habitId = h.id,
                key = HabitKey.valueOf(h.key),
                titleRu = h.titleRu,
                type = h.type,
                checkedDays = checkedByHabit[h.id] ?: 0,
                totalDays = totalDays
            )
        }
    }

    suspend fun getDailyStats(start: LocalDate, end: LocalDate): List<DailyCompletionStat> {
        val entries = dayEntryDao.getEntriesBetween(start, end)

        val checkedByDate = entries
            .filter { it.value }
            .groupingBy { it.date }
            .eachCount()

        val result = mutableListOf<DailyCompletionStat>()
        var current = start

        while (!current.isAfter(end)) {
            result += DailyCompletionStat(
                date = current,
                completedCount = checkedByDate[current] ?: 0
            )
            current = current.plusDays(1)
        }

        return result
    }

    suspend fun getAllTimeRange(): Pair<LocalDate, LocalDate> {
        val min = dayEntryDao.getMinDate() ?: LocalDate.now()
        return min to LocalDate.now()
    }

    suspend fun getFirstEntryDate(): LocalDate? {
        return dayEntryDao.getMinDate()
    }
}