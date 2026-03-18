package com.example.bebetterapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.bebetterapp.data.db.entity.HabitEntity

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY id ASC")
    fun observeActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY id ASC")
    suspend fun getActiveHabits(): List<HabitEntity>

    @Query("SELECT COUNT(*) FROM habits")
    suspend fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(habits: List<HabitEntity>)
}