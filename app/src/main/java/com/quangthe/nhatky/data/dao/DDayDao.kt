package com.quangthe.nhatky.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.quangthe.nhatky.data.entities.DDayEntity

@Dao
interface DDayDao {
    @Query("SELECT * FROM d_day ORDER BY sequence DESC")
    fun getAllDDay(): Flow<List<DDayEntity>>

    @Query("SELECT * FROM d_day")
    suspend fun getAllDDaySync(): List<DDayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDDay(dDay: DDayEntity)

    @Update
    suspend fun updateDDay(dDay: DDayEntity)

    @Query("DELETE FROM d_day WHERE sequence = :sequence")
    suspend fun deleteDDay(sequence: Int)
}
