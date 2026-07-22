package com.quangthe.nhatky.commons.utils

import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.ui.models.DiaryUiModel

val easyDiaryMimeType: String
    get() = "text/aaf_v27"

val easyDiaryMimeTypeAll: Array<String?>
    get() {
        val currentVersion = 27
        val easyDiaryMimeType = arrayOfNulls<String>(currentVersion)
        for (i in 0 until currentVersion) {
            easyDiaryMimeType[i] = "text/aaf_v" + (i + 1)
        }
        return easyDiaryMimeType
    }

fun summaryDiaryLabel(diary: Diary): String {
    val title = diary.title
    return if (!title.isNullOrBlank()) {
        title
    } else {
        val firstLine = diary.contents?.lines()?.firstOrNull { it.isNotBlank() }
        if (!firstLine.isNullOrBlank()) {
            firstLine
        } else if (diary.location != null) {
            diary.location?.address ?: "Vị trí đã chia sẻ"
        } else {
            "Nhật ký không tiêu đề"
        }
    }
}

fun summaryDiaryLabel(diary: DiaryUiModel): String {
    return if (diary.title.isNotBlank()) {
        diary.title
    } else {
        val firstLine = diary.contents.lines().firstOrNull { it.isNotBlank() }
        if (!firstLine.isNullOrBlank()) {
            firstLine
        } else {
            "Nhật ký không tiêu đề"
        }
    }
}

fun searchWordIndexes(
    contents: String,
    searchWord: String,
): List<Int> {
    val indexes = arrayListOf<Int>()
    if (searchWord.isNotEmpty()) {
        var index = contents.indexOf(searchWord, 0, true)
        while (index >= 0) {
            indexes.add(index)
            index = contents.indexOf(searchWord, index.plus(1), true)
        }
    }
    return indexes
}
