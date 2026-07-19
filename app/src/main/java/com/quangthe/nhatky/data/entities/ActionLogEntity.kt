package com.quangthe.nhatky.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "action_logs")
data class ActionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val sequence: Int = 0,
    val className: String?,
    val signature: String?,
    val key: String?,
    val value: String?
)
