package com.quangthe.nhatky.ui.models

data class DiaryUiModel(
    val sequence: Int,
    val title: String,
    val contents: String,
    val dateString: String,
    val currentTimeMillis: Long,
    val isAllDay: Boolean
)
