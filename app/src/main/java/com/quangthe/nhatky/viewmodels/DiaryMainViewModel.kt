package com.quangthe.nhatky.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.quangthe.nhatky.R
import com.quangthe.nhatky.enums.DiaryEntryType
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.models.NoteFolder
import com.quangthe.nhatky.models.SimpleNote
import com.quangthe.nhatky.models.TodoTask
import com.quangthe.nhatky.ui.models.DiaryMainItem
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.repositories.NoteRepository
import com.quangthe.nhatky.repositories.TaskRepository

class DiaryMainViewModel : ViewModel() {
    private val diaryRepository = DiaryRepository()
    private val noteRepository = NoteRepository()
    private val taskRepository = TaskRepository()

    init {
        viewModelScope.launch {
            delay(500L)
            markAsReady()
        }
    }

    private val _entryType = MutableStateFlow(DiaryEntryType.DIARY)
    val entryType: StateFlow<DiaryEntryType> = _entryType.asStateFlow()

    fun setEntryType(type: DiaryEntryType) {
        _entryType.value = type
        if (type != DiaryEntryType.NOTE) {
            _noteFolderStack.value = emptyList()
        }
        findDiary(_currentQuery.value)
    }

    private val _isReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    fun markAsReady() {
        _isReady.value = true
    }

    private val _currentQuery = MutableStateFlow("")
    val currentQuery: StateFlow<String> = _currentQuery.asStateFlow()

    fun setCurrentQuery(currentQuery: String) {
        _currentQuery.value = currentQuery
    }

    private val _diaryItems = MutableStateFlow<List<DiaryMainItem>>(listOf())
    val diaryItems: StateFlow<List<DiaryMainItem>> = _diaryItems.asStateFlow()

    private val _isTimelineMode = MutableStateFlow(false)
    val isTimelineMode: StateFlow<Boolean> = _isTimelineMode.asStateFlow()

    fun setTimelineMode(isTimeline: Boolean) {
        _isTimelineMode.value = isTimeline
    }

    private val _noteFolderStack = MutableStateFlow<List<NoteFolder>>(emptyList())
    val noteFolderStack: StateFlow<List<NoteFolder>> = _noteFolderStack.asStateFlow()

    init {
        findDiary()
    }

    fun setDiaryItems(items: List<DiaryMainItem>) {
        _diaryItems.value = items
    }

    fun enterNoteFolder(folder: NoteFolder) {
        _noteFolderStack.value = _noteFolderStack.value + folder
        findDiary(_currentQuery.value)
    }

    fun leaveNoteFolder() {
        val stack = _noteFolderStack.value.toMutableList()
        if (stack.isNotEmpty()) {
            stack.removeAt(stack.lastIndex)
            _noteFolderStack.value = stack
            findDiary(_currentQuery.value)
        }
    }

    fun isInNoteRootFolder(): Boolean = _noteFolderStack.value.isEmpty()

    fun getNoteFolderPath(rootName: String): String {
        val stack = _noteFolderStack.value
        if (stack.isEmpty()) return rootName
        return stack.joinToString(" > ") { it.name }
    }

    private fun getCurrentNoteFolderId(): Int {
        val stack = _noteFolderStack.value
        return if (stack.isNotEmpty()) stack.last().id else 0
    }

    fun findDiary(query: String? = null) {
        setCurrentQuery(query ?: "")
        val type = _entryType.value
        viewModelScope.launch {
            val items = mutableListOf<DiaryMainItem>()
            when (type) {
                DiaryEntryType.NOTE -> {
                    val folderId = getCurrentNoteFolderId()
                    val folders = noteRepository.findNoteFoldersByParent(folderId)
                        .filter { query.isNullOrEmpty() || it.name.contains(query, ignoreCase = true) }
                        .sortedByDescending { it.createdAt }

                    folders.forEach { items.add(DiaryMainItem.NoteFolderEntry(it)) }

                    val notes = noteRepository.findNotesByFolder(folderId)
                        .filter { query.isNullOrEmpty() || (it.title?.contains(query, ignoreCase = true) == true || it.contents?.contains(query, ignoreCase = true) == true) }
                        .sortedWith(compareByDescending<SimpleNote> { it.isPinned }.thenByDescending { it.updatedAt })

                    notes.forEach { items.add(DiaryMainItem.NoteEntry(it)) }
                }
                DiaryEntryType.TASK -> {
                    val tasks = taskRepository.findAllTasks()
                        .filter { query.isNullOrEmpty() || (it.title?.contains(query, ignoreCase = true) == true) }
                    
                    val activeTasks = tasks.filter { !it.isCompleted }
                        .sortedWith(compareByDescending<TodoTask> { it.priority }.thenByDescending { it.updatedAt })
                    
                    val completedTasks = tasks.filter { it.isCompleted }
                        .sortedByDescending { it.updatedAt }

                    activeTasks.forEach { items.add(DiaryMainItem.TaskEntry(it)) }
                    if (completedTasks.isNotEmpty()) {
                        items.add(DiaryMainItem.Header(R.string.completed_tasks, completedTasks.size))
                        completedTasks.forEach { items.add(DiaryMainItem.TaskEntry(it)) }
                    }
                }
                else -> {
                    diaryRepository.findDiary(query, entryType = 0).forEach {
                        items.add(DiaryMainItem.DiaryEntry(it))
                    }
                }
            }
            setDiaryItems(items)
        }
    }

    fun toggleTask(task: TodoTask) {
        viewModelScope.launch {
            taskRepository.toggleTask(task.sequence)
            findDiary(_currentQuery.value)
        }
    }

    fun deleteItem(item: DiaryMainItem) {
        viewModelScope.launch {
            when (item) {
                is DiaryMainItem.DiaryEntry -> diaryRepository.deleteDiary(item.diary.sequence)
                is DiaryMainItem.NoteEntry -> noteRepository.deleteNote(item.note.sequence)
                is DiaryMainItem.TaskEntry -> taskRepository.deleteTask(item.task.sequence)
                is DiaryMainItem.NoteFolderEntry -> noteRepository.deleteFolder(item.folder.id)
                is DiaryMainItem.Header -> {} // Do nothing
            }
            findDiary(_currentQuery.value)
        }
    }
}
