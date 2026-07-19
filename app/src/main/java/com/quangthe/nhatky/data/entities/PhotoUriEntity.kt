package com.quangthe.nhatky.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photo_uris",
    foreignKeys = [
        ForeignKey(
            entity = DiaryEntity::class,
            parentColumns = ["sequence"],
            childColumns = ["diarySequence"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["diarySequence"])]
)
data class PhotoUriEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val diarySequence: Int,
    val photoUri: String?,
    val mimeType: String?
)
