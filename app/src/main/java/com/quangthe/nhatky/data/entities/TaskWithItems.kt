package com.quangthe.nhatky.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithItems(
    @Embedded val task: TodoTaskEntity,
    @Relation(
        parentColumn = "sequence",
        entityColumn = "taskSequence"
    )
    val items: List<TodoItemEntity>
)
