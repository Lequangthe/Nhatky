package com.quangthe.nhatky.data.mapper

import com.quangthe.nhatky.data.entities.NoteFolderEntity
import com.quangthe.nhatky.data.entities.SimpleNoteEntity
import com.quangthe.nhatky.models.NoteFolder
import com.quangthe.nhatky.models.SimpleNote

fun SimpleNoteEntity.toDomain(): SimpleNote {
    val note = SimpleNote()
    note.sequence = this.sequence
    note.title = this.title
    note.contents = this.contents
    note.color = this.color
    note.isPinned = this.isPinned
    note.folderId = this.folderId
    note.createdAt = this.createdAt
    note.updatedAt = this.updatedAt
    return note
}

fun SimpleNote.toEntity(): SimpleNoteEntity {
    return SimpleNoteEntity(
        sequence = this.sequence,
        title = this.title,
        contents = this.contents,
        color = this.color,
        isPinned = this.isPinned,
        folderId = this.folderId,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun NoteFolderEntity.toDomain(): NoteFolder {
    val folder = NoteFolder()
    folder.id = this.id
    folder.name = this.name
    folder.color = this.color
    folder.parentId = this.parentId
    folder.sortOrder = this.sortOrder
    folder.createdAt = this.createdAt
    return folder
}

fun NoteFolder.toEntity(): NoteFolderEntity {
    return NoteFolderEntity(
        id = this.id,
        name = this.name,
        color = this.color,
        parentId = this.parentId,
        sortOrder = this.sortOrder,
        createdAt = this.createdAt
    )
}
