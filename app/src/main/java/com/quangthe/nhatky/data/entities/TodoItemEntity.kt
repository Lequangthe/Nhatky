package com.quangthe.nhatky.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "todo_items",
    foreignKeys = [
        ForeignKey(
            entity = TodoTaskEntity::class,
            parentColumns = ["sequence"],
            childColumns = ["taskSequence"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskSequence"])]
)
data class TodoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskSequence: Int,
    val text: String,
    val isChecked: Boolean
)
