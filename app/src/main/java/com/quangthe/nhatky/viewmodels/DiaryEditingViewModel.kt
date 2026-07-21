package com.quangthe.nhatky.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quangthe.nhatky.commons.utils.copyUriToInternalStorage
import com.quangthe.nhatky.core.config.DIARY_AUDIO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_PHOTO_DIRECTORY
import com.quangthe.nhatky.core.config.DIARY_VIDEO_DIRECTORY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.models.Location
import com.quangthe.nhatky.models.PhotoUri
import com.quangthe.nhatky.repositories.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiaryEditingViewModel : ViewModel() {
    private val diaryRepository = DiaryRepository()

    private val _diary = MutableStateFlow(Diary())
    val diary: StateFlow<Diary> = _diary.asStateFlow()

    fun loadDiary(sequence: Int, context: Context? = null) {
        viewModelScope.launch {
            diaryRepository.findDiaryBy(sequence)?.let { loadedDiary ->
                val needsMigration = context != null && loadedDiary.photoUris?.any { it.isContentUri() } == true
                if (needsMigration && context != null) {
                    val migratedPhotos = loadedDiary.photoUris?.map { photo ->
                        if (photo.isContentUri()) {
                            val uri = Uri.parse(photo.photoUri)
                            val mimeType = photo.mimeType ?: "image/jpeg"
                            val subDir = when {
                                mimeType.startsWith("video") -> DIARY_VIDEO_DIRECTORY
                                mimeType.startsWith("audio") -> DIARY_AUDIO_DIRECTORY
                                else -> DIARY_PHOTO_DIRECTORY
                            }
                            val internalPath = copyUriToInternalStorage(context, uri, subDir)
                            if (internalPath != null) PhotoUri(internalPath, mimeType) else photo
                        } else photo
                    }?.toMutableList()
                    val migratedDiary = loadedDiary.copy(photoUris = migratedPhotos)
                    _diary.value = migratedDiary
                    diaryRepository.updateDiary(migratedDiary)
                } else {
                    _diary.value = loadedDiary
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _diary.value = _diary.value.copy(title = title)
    }

    fun updateContents(contents: String) {
        _diary.value = _diary.value.copy(contents = contents)
    }

    fun updateLocation(location: Location) {
        _diary.value = _diary.value.copy(location = location)
    }

    fun addPhotoUris(uris: List<PhotoUri>) {
        val currentPhotos = _diary.value.photoUris?.toMutableList() ?: mutableListOf()
        // Ensure no duplicates
        uris.forEach { newUri ->
            if (currentPhotos.none { it.photoUri == newUri.photoUri }) {
                currentPhotos.add(newUri)
            }
        }
        _diary.value = _diary.value.copy(photoUris = currentPhotos)
    }

    fun addMediaUris(context: Context, uris: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newMedia = uris.mapNotNull { uri ->
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                val subDir = when {
                    mimeType.startsWith("video") -> DIARY_VIDEO_DIRECTORY
                    mimeType.startsWith("audio") -> DIARY_AUDIO_DIRECTORY
                    else -> DIARY_PHOTO_DIRECTORY
                }
                val internalPath = copyUriToInternalStorage(context, uri, subDir)
                if (internalPath != null) {
                    PhotoUri(internalPath, mimeType)
                } else null
            }

            withContext(Dispatchers.Main) {
                addPhotoUris(newMedia)
            }
        }
    }

    fun removePhotoUri(photoUri: PhotoUri) {
        val currentPhotos = _diary.value.photoUris?.toMutableList() ?: mutableListOf()
        currentPhotos.remove(photoUri)
        _diary.value = _diary.value.copy(photoUris = currentPhotos)
    }

    fun removeLocation() {
        _diary.value = _diary.value.copy(location = null)
    }

    fun saveDiary() {
        viewModelScope.launch {
            if (_diary.value.sequence > 0) {
                diaryRepository.updateDiary(_diary.value)
            } else {
                diaryRepository.insertDiary(_diary.value)
            }
        }
    }
}
