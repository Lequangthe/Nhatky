package com.quangthe.nhatky.data.mapper

import com.quangthe.nhatky.data.entities.TodoItemEntity
import com.quangthe.nhatky.data.entities.TodoTaskEntity
import com.quangthe.nhatky.models.TodoItem
import com.quangthe.nhatky.models.TodoTask

fun TodoTaskEntity.toDomain(items: List<TodoItemEntity>): TodoTask {
    val task = TodoTask()
    task.sequence = this.sequence
    task.title = this.title
    task.isCompleted = this.isCompleted
    task.priority = this.priority
    task.dueDate = this.dueDate
    task.createdAt = this.createdAt
    task.updatedAt = this.updatedAt
    
    val list = mutableListOf<TodoItem>()
    items.forEach { 
        val item = TodoItem()
        item.text = it.text
        item.isChecked = it.isChecked
        list.add(item)
    }
    task.items = list
    return task
}

fun TodoTask.toEntity(): TodoTaskEntity {
    return TodoTaskEntity(
        sequence = this.sequence,
        title = this.title,
        isCompleted = this.isCompleted,
        priority = this.priority,
        dueDate = this.dueDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun TodoItem.toEntity(taskSequence: Int): TodoItemEntity {
    return TodoItemEntity(
        taskSequence = taskSequence,
        text = this.text,
        isChecked = this.isChecked
    )
}
