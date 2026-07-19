package com.quangthe.nhatky.data.dao

import androidx.room.*
import com.quangthe.nhatky.data.entities.ActionLogEntity

@Dao
interface ActionLogDao {
    @Query("SELECT * FROM action_logs ORDER BY sequence DESC")
    suspend fun getAllLogs(): List<ActionLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActionLogEntity)

    @Query("DELETE FROM action_logs")
    suspend fun clearLogs()
}
