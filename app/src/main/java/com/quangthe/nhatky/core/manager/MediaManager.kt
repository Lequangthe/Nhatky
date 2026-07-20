package com.quangthe.nhatky.core.manager

import android.content.Context
import android.net.Uri
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.core.config.DIARY_AUDIO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_PHOTO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_VIDEO_DIRECTORY
import com.quangthe.nhatky.core.config.FILE_URI_PREFIX
import com.quangthe.nhatky.models.PhotoUri
import com.quangthe.nhatky.repositories.DiaryRepository
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.util.*

object MediaManager {
    private val diaryRepository = DiaryRepository()

    fun attachMedia(
        context: Context,
        selectPaths: List<String>,
        isUriString: Boolean,
        onProgress: (Boolean) -> Unit,
        onMediaAttached: (PhotoUri) -> Unit,
    ) {
        onProgress(true)
        Thread {
            selectPaths.forEach { item ->
                try {
                    val uri = if (isUriString) Uri.parse(item) else Uri.fromFile(File(item))
                    val mimeType = context.contentResolver.getType(uri) ?: guessMimeType(item)

                    val destDirectory = when {
                        mimeType.startsWith("video") -> DIARY_VIDEO_DIRECTORY
                        mimeType.startsWith("audio") -> DIARY_AUDIO_DIRECTORY
                        else -> DIARY_PHOTO_DIRECTORY
                    }

                    val fileName = UUID.randomUUID().toString()
                    val destPath = EasyDiaryUtils.getApplicationDataDirectory(context) + destDirectory + fileName
                    val destFile = File(destPath)

                    val finalMimeType: String
                    if (mimeType.startsWith("image")) {
                        finalMimeType = EasyDiaryUtils.downSamplingImage(context, uri, destFile)
                    } else {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(destFile).use { outputStream ->
                                IOUtils.copy(inputStream, outputStream)
                            }
                        }
                        finalMimeType = mimeType
                    }

                    val photoUriDto = PhotoUri(FILE_URI_PREFIX + destPath, finalMimeType)
                    onMediaAttached(photoUriDto)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            onProgress(false)
        }.start()
    }

    suspend fun cleanupOrphanedFiles(context: Context): Int {
        val allPhotoUris = diaryRepository.getAllPhotoUris()
        val linkedFileNames = allPhotoUris.mapNotNull { pu ->
            pu.photoUri?.substringAfterLast("/")
        }.toSet()

        val baseDir = EasyDiaryUtils.getApplicationDataDirectory(context)
        val dirs = listOf(DIARY_PHOTO_DIRECTORY, DIARY_AUDIO_DIRECTORY, DIARY_VIDEO_DIRECTORY)
        var deletedCount = 0

        dirs.forEach { dirName ->
            val dir = File(baseDir + dirName)
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.forEach { file ->
                    if (file.isFile && !linkedFileNames.contains(file.name)) {
                        if (file.delete()) {
                            deletedCount++
                        }
                    }
                }
            }
        }
        return deletedCount
    }

    suspend fun getOrphanedFilesCount(context: Context): Int {
        val allPhotoUris = diaryRepository.getAllPhotoUris()
        val linkedFileNames = allPhotoUris.mapNotNull { pu ->
            pu.photoUri?.substringAfterLast("/")
        }.toSet()

        val baseDir = EasyDiaryUtils.getApplicationDataDirectory(context)
        val dirs = listOf(DIARY_PHOTO_DIRECTORY, DIARY_AUDIO_DIRECTORY, DIARY_VIDEO_DIRECTORY)
        var count = 0

        dirs.forEach { dirName ->
            val dir = File(baseDir + dirName)
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.forEach { file ->
                    if (file.isFile && !linkedFileNames.contains(file.name)) {
                        count++
                    }
                }
            }
        }
        return count
    }

    private fun guessMimeType(path: String): String = when {
        path.contains("video") -> "video/mp4"
        path.contains("audio") -> "audio/mp4"
        else -> "image/jpeg"
    }
}
