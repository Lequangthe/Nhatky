package com.quangthe.nhatky.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.data.AppDatabase
import com.quangthe.nhatky.data.mapper.toDomain
import com.quangthe.nhatky.data.mapper.toEntity
import com.quangthe.nhatky.helper.EasyDiaryApplication
import com.quangthe.nhatky.models.TodoTask

class TaskRepository {
    private val database = AppDatabase.getInstance(EasyDiaryApplication.context!!)
    private val taskDao = database.taskDao()

    suspend fun findAllTasks(): List<TodoTask> = withContext(Dispatchers.IO) {
        taskDao.getAllTasksSync().map { it.task.toDomain(it.items) }
    }

    suspend fun findTaskBy(sequence: Int): TodoTask? = withContext(Dispatchers.IO) {
        taskDao.getTaskWithItemsBySequence(sequence)?.let { 
            it.task.toDomain(it.items)
        }
    }

    suspend fun saveTask(task: TodoTask) = withContext(Dispatchers.IO) {
        val sequence = taskDao.insertTask(task.toEntity()).toInt()
        taskDao.deleteItemsByTask(if (task.sequence > 0) task.sequence else sequence)
        val items = task.items.map { 
            com.quangthe.nhatky.data.entities.TodoItemEntity(
                taskSequence = if (task.sequence > 0) task.sequence else sequence,
                text = it.text,
                isChecked = it.isChecked
            )
        }
        if (items.isNotEmpty()) {
            taskDao.insertTaskItems(items)
        }
    }

    suspend fun deleteTask(sequence: Int) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(sequence)
    }

    suspend fun toggleTask(sequence: Int) = withContext(Dispatchers.IO) {
        val taskWithItems = taskDao.getTaskWithItemsBySequence(sequence) ?: return@withContext
        val task = taskWithItems.task
        val isCompleted = !task.isCompleted
        taskDao.updateTask(task.copy(isCompleted = isCompleted, updatedAt = System.currentTimeMillis()))
        
        val items = taskWithItems.items.map { it.copy(isChecked = isCompleted) }
        taskDao.deleteItemsByTask(sequence)
        taskDao.insertTaskItems(items)
    }
}
