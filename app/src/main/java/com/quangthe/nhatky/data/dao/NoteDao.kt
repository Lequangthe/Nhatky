package com.quangthe.nhatky.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.quangthe.nhatky.data.entities.NoteFolderEntity
import com.quangthe.nhatky.data.entities.SimpleNoteEntity

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<SimpleNoteEntity>>

    @Query("SELECT * FROM notes WHERE :folderId = -1 OR folderId = :folderId ORDER BY isPinned DESC, updatedAt DESC")
    suspend fun getNotesByFolder(folderId: Int): List<SimpleNoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SimpleNoteEntity)

    @Update
    suspend fun updateNote(note: SimpleNoteEntity)

    @Query("DELETE FROM notes WHERE sequence = :sequence")
    suspend fun deleteNote(sequence: Int)

    @Query("SELECT * FROM notes WHERE sequence = :sequence")
    suspend fun getNoteBySequence(sequence: Int): SimpleNoteEntity?

    @Query("SELECT * FROM note_folders ORDER BY sortOrder ASC, createdAt DESC")
    fun getAllFolders(): Flow<List<NoteFolderEntity>>

    @Query("SELECT * FROM note_folders WHERE parentId = :parentId ORDER BY sortOrder ASC, createdAt DESC")
    suspend fun getFoldersByParent(parentId: Int): List<NoteFolderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: NoteFolderEntity)

    @Update
    suspend fun updateFolder(folder: NoteFolderEntity)

    @Query("DELETE FROM note_folders WHERE id = :id")
    suspend fun deleteFolder(id: Int)
}
