package com.quangthe.nhatky.core.export

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.quangthe.nhatky.commons.utils.getApplicationDataDirectory
import com.quangthe.nhatky.core.application.EasyDiaryApplication
import com.quangthe.nhatky.core.config.DIARY_AUDIO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_PHOTO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_VIDEO_DIRECTORY
import com.quangthe.nhatky.data.AppDatabase
import com.quangthe.nhatky.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupManager {

    data class FullBackupData(
        val version: Int = 1,
        val diaries: List<DiaryEntity>,
        val photoUris: List<PhotoUriEntity>,
        val notes: List<SimpleNoteEntity>,
        val folders: List<NoteFolderEntity>,
        val tasks: List<TodoTaskEntity>,
        val taskItems: List<TodoItemEntity>,
        val dDays: List<DDayEntity>
    )

    suspend fun createFullBackup(context: Context, destUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getInstance(context)
            val data = FullBackupData(
                diaries = db.diaryDao().getAllDiariesSync().map { it.diary },
                photoUris = db.diaryDao().getAllPhotoUris(),
                notes = db.noteDao().getAllNotesSync(),
                folders = db.noteDao().getAllFoldersSync(),
                tasks = db.taskDao().getAllTasksSync().map { it.task },
                taskItems = db.taskDao().getAllTasksSync().flatMap { it.items },
                dDays = db.dDayDao().getAllDDaySync()
            )

            val json = Gson().toJson(data)
            
            context.contentResolver.openOutputStream(destUri)?.use { outputStream ->
                ZipOutputStream(outputStream).use { zos ->
                    // 1. Write JSON data
                    zos.putNextEntry(ZipEntry("backup_data.json"))
                    zos.write(json.toByteArray())
                    zos.closeEntry()

                    // 2. Include Media Files
                    val dataDir = getApplicationDataDirectory(context)
                    val mediaDirs = listOf(DIARY_PHOTO_DIRECTORY, DIARY_VIDEO_DIRECTORY, DIARY_AUDIO_DIRECTORY)
                    
                    mediaDirs.forEach { subDir ->
                        val dir = File(dataDir + subDir)
                        if (dir.exists()) {
                            dir.listFiles()?.forEach { file ->
                                if (file.isFile) {
                                    zos.putNextEntry(ZipEntry(subDir.removePrefix("/") + file.name))
                                    file.inputStream().use { it.copyTo(zos) }
                                    zos.closeEntry()
                                }
                            }
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun restoreFullBackup(context: Context, zipUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getInstance(context)
            val dataDir = getApplicationDataDirectory(context)
            var backupData: FullBackupData? = null

            context.contentResolver.openInputStream(zipUri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zis ->
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        when {
                            entry.name == "backup_data.json" -> {
                                val reader = InputStreamReader(zis)
                                backupData = Gson().fromJson(reader, FullBackupData::class.java)
                            }
                            entry.name.startsWith("AAFactory/EasyDiary/") -> {
                                val destFile = File(dataDir + "/" + entry.name)
                                destFile.parentFile?.mkdirs()
                                destFile.outputStream().use { zis.copyTo(it) }
                            }
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
            }

            backupData?.let { data ->
                db.runInTransaction {
                    // This is a destructive restore, clearing current data first
                    // You might want to ask user before this or merge instead
                    // For now, let's implement as overwrite for simplicity but ideally it should be merge
                    // db.clearAllTables() // Warning: This might clear tables not in backup
                    
                    // Better approach: overwrite items by primary key (Room REPLACE does this)
                    // Or clear specific tables
                    // Since we use REPLACE on Conflict, it will update existing ones.
                }
                
                // Perform inserts
                data.folders.forEach { db.noteDao().insertFolder(it) }
                data.notes.forEach { db.noteDao().insertNote(it) }
                data.diaries.forEach { db.diaryDao().insertDiary(it) }
                data.photoUris.chunked(100).forEach { db.diaryDao().insertPhotoUris(it) }
                data.tasks.forEach { db.taskDao().insertTask(it) }
                data.taskItems.chunked(100).forEach { db.taskDao().insertTaskItems(it) }
                data.dDays.forEach { db.dDayDao().insertDDay(it) }
                
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
