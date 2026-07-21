package com.quangthe.nhatky.commons.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import com.quangthe.nhatky.core.config.BACKUP_DB_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_AUDIO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_PHOTO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_VIDEO_DIRECTORY
import com.quangthe.nhatky.core.config.MARKDOWN_DIRECTORY
import com.quangthe.nhatky.core.config.USER_CUSTOM_FONTS_DIRECTORY
import java.io.File
import java.util.UUID

fun makeDirectory(path: String) {
    val workingDirectory = File(path)
    if (!workingDirectory.exists()) workingDirectory.mkdirs()
}

fun initWorkingDirectory(context: Context) {
    makeDirectory(getApplicationDataDirectory(context) + DIARY_PHOTO_DIRECTORY)
    makeDirectory(getApplicationDataDirectory(context) + MARKDOWN_DIRECTORY)
    makeDirectory(getApplicationDataDirectory(context) + BACKUP_DB_DIRECTORY)
    makeDirectory(getApplicationDataDirectory(context) + DIARY_AUDIO_DIRECTORY)
    makeDirectory(getApplicationDataDirectory(context) + DIARY_VIDEO_DIRECTORY)
    makeDirectory(getApplicationDataDirectory(context) + USER_CUSTOM_FONTS_DIRECTORY)
}

fun getExternalStorageDirectory(): File = Environment.getExternalStorageDirectory()

fun initLegacyWorkingDirectory(context: Context) {
}

fun getApplicationDataDirectory(context: Context): String {
    return context.applicationInfo.dataDir
}

fun readFileWithSAF(
    mimeType: String,
    activityResultLauncher: ActivityResultLauncher<Intent>,
) {
    val intent =
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = mimeType
        }
    activityResultLauncher.launch(intent)
}

fun writeFileWithSAF(
    fileName: String,
    mimeType: String,
    activityResultLauncher: ActivityResultLauncher<Intent>,
) {
    Intent(Intent.ACTION_CREATE_DOCUMENT)
        .apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }.run {
            activityResultLauncher.launch(this)
        }
}

fun queryName(
    resolver: ContentResolver,
    uri: Uri,
): String {
    val returnCursor: Cursor? = resolver.query(uri, null, null, null, null)
    var name: String? = null
    returnCursor?.let {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1) {
            it.moveToFirst()
            name = returnCursor.getString(nameIndex)
        }
        returnCursor.close()
    }
    return name ?: UUID.randomUUID().toString()
}

fun copyUriToInternalStorage(context: Context, uri: Uri, destSubDir: String): String? {
    val contentResolver = context.contentResolver
    val fileName = queryName(contentResolver, uri)
    val destDir = File(getApplicationDataDirectory(context) + destSubDir)
    if (!destDir.exists()) destDir.mkdirs()

    val destFile = File(destDir, "${UUID.randomUUID()}_$fileName")
    return try {
        contentResolver.openInputStream(uri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        destFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
