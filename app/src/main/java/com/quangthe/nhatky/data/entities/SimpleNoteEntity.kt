package com.quangthe.nhatky.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class SimpleNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val sequence: Int = 0,
    val title: String?,
    val contents: String?,
    val color: Int,
    val isPinned: Boolean,
    val folderId: Int,
    val createdAt: Long,
    val updatedAt: Long
)
