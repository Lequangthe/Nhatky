package com.quangthe.nhatky.commons.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.FileReader

fun sequenceToPageIndex(
    diaryList: List<com.quangthe.nhatky.models.Diary>,
    sequence: Int,
): Int {
    var pageIndex = 0
    if (sequence > -1) {
        for (i in diaryList.indices) {
            if (diaryList[i].sequence == sequence) {
                pageIndex = i
                break
            }
        }
    }
    return pageIndex
}

fun jsonStringToHashMap(jsonString: String): HashMap<String, Any> {
    val type = object : TypeToken<HashMap<String, Any>>() {}.type
    return GsonBuilder().create().fromJson(jsonString, type)
}

fun jsonFileToHashMap(filename: String): HashMap<String, Any> {
    val reader = JsonReader(FileReader(filename))
    val type = object : TypeToken<HashMap<String, Any>>() {}.type
    val map: HashMap<String, Any> = GsonBuilder().create().fromJson(reader, type)
    reader.close()
    return map
}

fun hashMapToJsonString(map: HashMap<String, Any>): String {
    val gson = GsonBuilder().setPrettyPrinting().create()
    return gson.toJson(map)
}

fun fromHtml(target: String): Spanned {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        @Suppress("DEPRECATION")
        Html.fromHtml(target)
    } else {
        Html.fromHtml(target, Html.FROM_HTML_MODE_LEGACY)
    }
}
