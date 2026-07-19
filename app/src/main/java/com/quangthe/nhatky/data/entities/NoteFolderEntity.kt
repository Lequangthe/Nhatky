package com.quangthe.nhatky.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_folders")
data class NoteFolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: Int,
    val parentId: Int,
    val sortOrder: Int,
    val createdAt: Long
)
