package com.quangthe.nhatky.models

data class SimpleNote(
    var sequence: Int = 0,
    var title: String? = null,
    var contents: String? = null,
    var color: Int = 0,
    var isPinned: Boolean = false,
    var folderId: Int = 0,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)
