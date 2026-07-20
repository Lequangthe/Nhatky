package com.quangthe.nhatky.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.models.Location
import com.quangthe.nhatky.models.PhotoUri
import com.quangthe.nhatky.repositories.DiaryRepository

class DiaryEditingViewModel : ViewModel() {
    private val diaryRepository = DiaryRepository()

    private val _diary = MutableStateFlow(Diary())
    val diary: StateFlow<Diary> = _diary.asStateFlow()

    fun loadDiary(sequence: Int) {
        viewModelScope.launch {
            diaryRepository.findDiaryBy(sequence)?.let {
                _diary.value = it
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
        currentPhotos.addAll(uris)
        _diary.value = _diary.value.copy(photoUris = currentPhotos)
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
