package com.quangthe.nhatky.core.export

import android.content.Context
import android.net.Uri
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.core.config.DateUtilConstants
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.repositories.NoteRepository
import com.quangthe.nhatky.repositories.TaskRepository
import java.io.FileOutputStream

object ExportManager {

    suspend fun exportToText(
        context: Context,
        uri: Uri,
        exportDiaries: Boolean,
        exportNotes: Boolean,
        exportTasks: Boolean
    ): Boolean {
        return try {
            val content = StringBuilder()
            content.append("--- EASY DIARY EXPORT ---\n")
            content.append("Date: ${DateUtils.timeMillisToDateTime(System.currentTimeMillis(), DateUtilConstants.DATE_PATTERN_DASH)}\n\n")

            if (exportDiaries) {
                val diaryRepository = DiaryRepository()
                content.append("=== DIARIES ===\n")
                diaryRepository.findDiary(null).forEach { diary ->
                    content.append("[${DateUtils.getDateStringFromTimeMillis(diary.currentTimeMillis)}] ${diary.title ?: "(No Title)"}\n")
                    content.append("${diary.contents}\n")
                    if (diary.location != null) {
                        content.append("Location: ${diary.location?.address} (${diary.location?.latitude}, ${diary.location?.longitude})\n")
                    }
                    content.append("----------------------------\n")
                }
                content.append("\n")
            }

            if (exportNotes) {
                val noteRepository = NoteRepository()
                content.append("=== NOTES ===\n")
                noteRepository.findAllNotes().forEach { note ->
                    content.append("${note.title ?: "(No Title)"}\n")
                    content.append("${note.contents}\n")
                    content.append("----------------------------\n")
                }
                content.append("\n")
            }

            if (exportTasks) {
                val taskRepository = TaskRepository()
                content.append("=== TASKS ===\n")
                taskRepository.findAllTasks().forEach { task ->
                    val status = if (task.isCompleted) "[X]" else "[ ]"
                    content.append("$status ${task.title ?: "(No Title)"}\n")
                    task.items.forEach { item ->
                        val itemStatus = if (item.isChecked) "  - [X]" else "  - [ ]"
                        content.append("$itemStatus ${item.text}\n")
                    }
                    content.append("----------------------------\n")
                }
            }

            context.contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { outputStream ->
                    outputStream.write(content.toString().toByteArray())
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
