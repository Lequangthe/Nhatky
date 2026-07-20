package com.quangthe.nhatky.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.data.AppDatabase
import com.quangthe.nhatky.data.mapper.toDomain
import com.quangthe.nhatky.data.mapper.toEntity
import com.quangthe.nhatky.core.application.EasyDiaryApplication
import com.quangthe.nhatky.models.NoteFolder
import com.quangthe.nhatky.models.SimpleNote

class NoteRepository {
    private val database = AppDatabase.getInstance(EasyDiaryApplication.context!!)
    private val noteDao = database.noteDao()

    suspend fun findAllNotes(): List<SimpleNote> = withContext(Dispatchers.IO) {
        // This is tricky because DAO returns Flow or List. Repository expects List.
        // We can't easily use Flow here without changing the whole architecture to Flow.
        // For now, I'll add a sync method to DAO.
        noteDao.getNotesByFolder(-1).map { it.toDomain() } // -1 for all
    }

    suspend fun findNoteBy(sequence: Int): SimpleNote? = withContext(Dispatchers.IO) {
        noteDao.getNoteBySequence(sequence)?.toDomain()
    }

    suspend fun findNotesByFolder(folderId: Int): List<SimpleNote> = withContext(Dispatchers.IO) {
        noteDao.getNotesByFolder(folderId).map { it.toDomain() }
    }

    suspend fun saveNote(note: SimpleNote) = withContext(Dispatchers.IO) {
        if (note.sequence > 0) {
            noteDao.updateNote(note.toEntity())
        } else {
            noteDao.insertNote(note.toEntity())
        }
    }

    suspend fun deleteNote(sequence: Int) = withContext(Dispatchers.IO) {
        noteDao.deleteNote(sequence)
    }

    suspend fun findNoteFoldersByParent(parentId: Int): List<NoteFolder> = withContext(Dispatchers.IO) {
        noteDao.getFoldersByParent(parentId).map { it.toDomain() }
    }

    suspend fun saveFolder(folder: NoteFolder) = withContext(Dispatchers.IO) {
        if (folder.id > 0) {
            noteDao.updateFolder(folder.toEntity())
        } else {
            noteDao.insertFolder(folder.toEntity())
        }
    }

    suspend fun deleteFolder(id: Int) = withContext(Dispatchers.IO) {
        noteDao.deleteFolder(id)
    }
}
