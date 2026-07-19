package com.quangthe.nhatky.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "d_day")
data class DDayEntity(
    @PrimaryKey(autoGenerate = true)
    val sequence: Int = 0,
    val targetTimeStamp: Long,
    val title: String?
)
