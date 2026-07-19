package com.quangthe.nhatky.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.quangthe.nhatky.models.DDay
import com.quangthe.nhatky.repositories.DDayRepository

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val dDayRepository = DDayRepository()

    val dDays: StateFlow<List<DDay>> = dDayRepository.getAllDDay()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteDDay(sequence: Int) {
        // Handle deletion
    }
}
