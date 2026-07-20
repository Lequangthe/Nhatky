package com.quangthe.nhatky.models

data class TodoTask(
    var sequence: Int = 0,
    var title: String? = null,
    var isCompleted: Boolean = false,
    var priority: Int = 0, // 0: None, 1: Low, 2: Medium, 3: High
    var dueDate: Long? = null,
    var items: MutableList<TodoItem> = mutableListOf(),
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) {
    fun toLegacyDiary(): Diary {
        val diary = Diary()
        diary.sequence = sequence
        diary.title = title
        diary.contents = items.joinToString("\n") { (if (it.isChecked) "[X] " else "[ ] ") + it.text }
        diary.currentTimeMillis = createdAt
        @Suppress("DEPRECATION")
        diary.entryType = 2 // TASK
        return diary
    }
}

data class TodoItem(
    var text: String = "",
    var isChecked: Boolean = false
)
