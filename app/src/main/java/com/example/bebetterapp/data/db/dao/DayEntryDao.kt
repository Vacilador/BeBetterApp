package com.example.bebetterapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bebetterapp.data.db.entity.DayEntryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DayEntryDao {

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM day_entries
            WHERE date = :date AND value = 1
            LIMIT 1
        )
        """
    )
    suspend fun hasAnyChecked(date: LocalDate): Boolean

    @Query("SELECT * FROM day_entries WHERE date = :date")
    fun observeEntriesForDate(date: LocalDate): Flow<List<DayEntryEntity>>

    @Query("SELECT * FROM day_entries WHERE date = :date")
    suspend fun getEntriesForDate(date: LocalDate): List<DayEntryEntity>

    @Query(
        """
        SELECT * FROM day_entries
        WHERE habitId = :habitId AND date = :date
        LIMIT 1
        """
    )
    suspend fun getEntry(habitId: Long, date: LocalDate): DayEntryEntity?

    @Query(
        """
        SELECT * FROM day_entries
        WHERE date BETWEEN :start AND :end
        """
    )
    suspend fun getEntriesBetween(start: LocalDate, end: LocalDate): List<DayEntryEntity>

    @Query("SELECT MIN(date) FROM day_entries")
    suspend fun getMinDate(): LocalDate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: DayEntryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<DayEntryEntity>)
}
