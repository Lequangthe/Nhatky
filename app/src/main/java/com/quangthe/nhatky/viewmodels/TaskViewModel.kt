package com.quangthe.nhatky.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.quangthe.nhatky.models.TodoItem
import com.quangthe.nhatky.models.TodoTask
import com.quangthe.nhatky.repositories.TaskRepository

class TaskViewModel : ViewModel() {
    private val taskRepository = TaskRepository()
    private val _task = MutableStateFlow<TodoTask?>(null)
    val task: StateFlow<TodoTask?> = _task

    fun loadTask(sequence: Int) {
        viewModelScope.launch {
            if (sequence > 0) {
                _task.value = taskRepository.findTaskBy(sequence)
            } else {
                _task.value = TodoTask()
            }
        }
    }

    fun saveTask(title: String, items: List<Pair<String, Boolean>>, priority: Int = 0) {
        val currentTask = _task.value ?: TodoTask()
        currentTask.title = title
        currentTask.priority = priority
        val taskItems = mutableListOf<TodoItem>()
        items.forEach { (text, isChecked) ->
            taskItems.add(TodoItem().apply {
                this.text = text
                this.isChecked = isChecked
            })
        }
        currentTask.items = taskItems
        currentTask.isCompleted = taskItems.isNotEmpty() && taskItems.all { it.isChecked }

        viewModelScope.launch {
            taskRepository.saveTask(currentTask)
        }
    }
}
