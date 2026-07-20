package com.quangthe.nhatky.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.core.config.BACKUP_DB_DIRECTORY
import com.quangthe.nhatky.core.config.CAPTURE_CAMERA_FILE_NAME
import com.quangthe.nhatky.core.config.DIARY_PHOTO_DIRECTORY
import com.quangthe.nhatky.core.config.MIME_TYPE_BINARY
import com.quangthe.nhatky.R
import java.io.File
import java.io.FileOutputStream

fun Context.getUriForFile(targetFile: File): Uri {
    val authority = "${this.packageName}.provider"
    return if (Build.VERSION.SDK_INT >
        Build.VERSION_CODES.M
    ) {
        FileProvider.getUriForFile(this, authority, targetFile)
    } else {
        Uri.fromFile(targetFile)
    }
}

fun Context.createTemporaryPhotoFile(
    uri: Uri? = null,
    fromUri: Boolean = false,
): File {
    val temporaryFile = File(EasyDiaryUtils.getApplicationDataDirectory(this) + DIARY_PHOTO_DIRECTORY, CAPTURE_CAMERA_FILE_NAME)
    if (temporaryFile.exists()) temporaryFile.delete()

    when (fromUri) {
        true -> {
            contentResolver.openInputStream(uri!!)?.use { inputStream ->
                FileOutputStream(temporaryFile.absoluteFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        false -> {
            temporaryFile.createNewFile()
        }
    }

    return temporaryFile
}

fun Context.shareFile(targetFile: File) {
    shareFile(targetFile, contentResolver.getType(getUriForFile(targetFile)) ?: MIME_TYPE_BINARY)
}

fun Context.shareFile(
    targetFile: File,
    mimeType: String,
) {
    Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, getUriForFile(targetFile))
        type = mimeType
        startActivity(Intent.createChooser(this, getString(R.string.diary_card_share_info)))
    }
}

fun Context.exportDatabaseFile() {
    val srcFile = getDatabasePath("easydiary.db")
    val destFilePath = BACKUP_DB_DIRECTORY + "easydiary.db_" + DateUtils.getCurrentDateTime("yyyyMMdd_HHmmss")
    val destFile = File(EasyDiaryUtils.getApplicationDataDirectory(this) + destFilePath)
    srcFile.copyTo(destFile, overwrite = true)
    config.diaryBackupLocal = System.currentTimeMillis()
}
