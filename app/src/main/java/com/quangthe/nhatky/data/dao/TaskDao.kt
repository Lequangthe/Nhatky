package com.quangthe.nhatky.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.quangthe.nhatky.data.entities.TaskWithItems
import com.quangthe.nhatky.data.entities.TodoItemEntity
import com.quangthe.nhatky.data.entities.TodoTaskEntity

@Dao
interface TaskDao {
    @Transaction
    @Query("SELECT * FROM todo_tasks ORDER BY updatedAt DESC, sequence DESC")
    fun getAllTasksWithItems(): Flow<List<TaskWithItems>>

    @Query("SELECT * FROM todo_tasks ORDER BY updatedAt DESC, sequence DESC")
    suspend fun getAllTasksSync(): List<TaskWithItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TodoTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItems(items: List<TodoItemEntity>)

    @Query("DELETE FROM todo_tasks WHERE sequence = :sequence")
    suspend fun deleteTask(sequence: Int)

    @Update
    suspend fun updateTask(task: TodoTaskEntity)

    @Query("SELECT * FROM todo_tasks WHERE sequence = :sequence")
    suspend fun getTaskBySequence(sequence: Int): TodoTaskEntity?

    @Query("SELECT * FROM todo_tasks WHERE sequence = :sequence")
    suspend fun getTaskWithItemsBySequence(sequence: Int): TaskWithItems?

    @Query("DELETE FROM todo_items WHERE taskSequence = :taskSequence")
    suspend fun deleteItemsByTask(taskSequence: Int)
}
