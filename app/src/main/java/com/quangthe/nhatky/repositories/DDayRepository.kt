package com.quangthe.nhatky.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.data.AppDatabase
import com.quangthe.nhatky.data.mapper.toDomain
import com.quangthe.nhatky.data.mapper.toEntity
import com.quangthe.nhatky.helper.EasyDiaryApplication
import com.quangthe.nhatky.models.DDay

class DDayRepository {
    private val database = AppDatabase.getInstance(EasyDiaryApplication.context!!)
    private val dDayDao = database.dDayDao()

    fun getAllDDay(): Flow<List<DDay>> {
        return dDayDao.getAllDDay().map { entities -> entities.map { it.toDomain() } }
    }

    suspend fun findAllDDay(ascending: Boolean = false): List<DDay> = withContext(Dispatchers.IO) {
        val list = dDayDao.getAllDDaySync().map { it.toDomain() }
        if (ascending) list.sortedBy { it.sequence }
        else list.sortedByDescending { it.sequence }
    }

    suspend fun saveDDay(dDay: DDay) = withContext(Dispatchers.IO) {
        dDayDao.insertDDay(dDay.toEntity())
    }

    suspend fun deleteDDay(sequence: Int) = withContext(Dispatchers.IO) {
        dDayDao.deleteDDay(sequence)
    }
}
