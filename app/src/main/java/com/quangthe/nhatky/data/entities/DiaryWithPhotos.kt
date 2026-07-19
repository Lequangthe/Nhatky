package com.quangthe.nhatky.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class DiaryWithPhotos(
    @Embedded val diary: DiaryEntity,
    @Relation(
        parentColumn = "sequence",
        entityColumn = "diarySequence"
    )
    val photoUris: List<PhotoUriEntity>
)
