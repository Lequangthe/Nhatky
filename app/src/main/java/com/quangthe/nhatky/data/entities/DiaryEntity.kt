package com.quangthe.nhatky.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diaries")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val sequence: Int = 0,
    val currentTimeMillis: Long,
    val title: String?,
    val contents: String?,
    val dateString: String?,
    val isAllDay: Boolean,
    val isEncrypt: Boolean,
    val encryptKeyHash: String?,
    val isHoliday: Boolean,
    val address: String?,
    val latitude: Double,
    val longitude: Double
)
