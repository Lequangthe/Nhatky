package com.quangthe.nhatky.enums

enum class DiaryEntryType(val value: Int) {
    DIARY(0),
    NOTE(1),
    TASK(2);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: DIARY
    }
}
