package com.quangthe.nhatky.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_tasks")
data class TodoTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val sequence: Int = 0,
    val title: String?,
    val isCompleted: Boolean,
    val priority: Int,
    val dueDate: Long?,
    val createdAt: Long,
    val updatedAt: Long
)
