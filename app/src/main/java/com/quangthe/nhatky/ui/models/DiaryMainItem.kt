package com.quangthe.nhatky.ui.models

import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.models.NoteFolder
import com.quangthe.nhatky.models.SimpleNote
import com.quangthe.nhatky.models.TodoTask

sealed class DiaryMainItem {
    data class DiaryEntry(val diary: Diary) : DiaryMainItem()
    data class NoteEntry(val note: SimpleNote) : DiaryMainItem()
    data class TaskEntry(val task: TodoTask) : DiaryMainItem()
    data class NoteFolderEntry(val folder: NoteFolder) : DiaryMainItem()
    data class Header(val titleRes: Int, val count: Int = 0) : DiaryMainItem()
}
