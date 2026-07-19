package com.quangthe.nhatky.models

data class NoteFolder(
    var id: Int = 0,
    var name: String = "",
    var color: Int = 0,
    var parentId: Int = 0,
    var sortOrder: Int = 0,
    var createdAt: Long = System.currentTimeMillis()
)
