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
    return if (diary.title.isNullOrEmpty()) diary.contents!!.lines()[0] else diary.title!!
}

fun summaryDiaryLabel(diary: DiaryUiModel): String {
    return if (diary.title.isNullOrEmpty()) diary.contents!!.lines()[0] else diary.title!!
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
