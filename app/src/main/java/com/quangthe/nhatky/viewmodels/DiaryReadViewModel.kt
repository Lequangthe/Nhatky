package com.quangthe.nhatky.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.enums.DiaryEntryType
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.helper.DiaryComponentConstants
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.repositories.DiaryRepository

class DiaryReadViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val diaryRepository = DiaryRepository()
    val config = application.config
    private val _isShowContentsCounting: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isShowContentsCounting: StateFlow<Boolean> = _isShowContentsCounting.asStateFlow()

    private val _isShowAddress: MutableStateFlow<Boolean> = MutableStateFlow(config.enableLocationInfo)
    val isShowAddress: StateFlow<Boolean> = _isShowAddress.asStateFlow()

    private val _diaryList: MutableStateFlow<List<Diary>> = MutableStateFlow(emptyList())
    val diaryList: StateFlow<List<Diary>> = _diaryList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _diary = MutableStateFlow(Diary())
    val diary: StateFlow<Diary> = _diary.asStateFlow()

    fun loadDiary(sequence: Int) {
        viewModelScope.launch {
            diaryRepository.findDiaryBy(sequence)?.let {
                _diary.value = it
            }
        }
    }

    fun applyFilter(mode: String?) {
        _isLoading.value = true
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                when (mode) {
                    DiaryComponentConstants.MODE_TASK_TODO -> {
                        diaryRepository.findDiary(null, entryType = DiaryEntryType.TASK.value).reversed()
                    }
                    DiaryComponentConstants.MODE_TASK_DOING -> {
                        diaryRepository.findDiary(null, entryType = DiaryEntryType.TASK.value)
                    }
                    DiaryComponentConstants.MODE_TASK_DONE -> {
                        diaryRepository.findDiary(null, entryType = DiaryEntryType.TASK.value)
                    }
                    DiaryComponentConstants.MODE_TASK_CANCEL -> {
                        diaryRepository.findDiary(null, entryType = DiaryEntryType.TASK.value)
                    }
                    DiaryComponentConstants.MODE_FUTURE -> {
                        diaryRepository.findDiary(null, entryType = DiaryEntryType.DIARY.value)
                            .filter { it.currentTimeMillis > System.currentTimeMillis() }
                            .reversed()
                    }
                    else -> {
                        diaryRepository.findDiary(
                            query = mode,
                            entryType = 0
                        )
                    }
                }
            }
            _diaryList.value = list
            _isLoading.value = false
        }
    }
}
