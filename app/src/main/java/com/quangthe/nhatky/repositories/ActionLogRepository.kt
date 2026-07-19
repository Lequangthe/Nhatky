package com.quangthe.nhatky.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.data.AppDatabase
import com.quangthe.nhatky.data.mapper.toDomain
import com.quangthe.nhatky.data.mapper.toEntity
import com.quangthe.nhatky.helper.EasyDiaryApplication
import com.quangthe.nhatky.models.ActionLog

class ActionLogRepository {
    private val database = AppDatabase.getInstance(EasyDiaryApplication.context!!)
    private val actionLogDao = database.actionLogDao()

    suspend fun getAllLogs(): List<ActionLog> = withContext(Dispatchers.IO) {
        actionLogDao.getAllLogs().map { it.toDomain() }
    }

    suspend fun insertLog(log: ActionLog) = withContext(Dispatchers.IO) {
        actionLogDao.insertLog(log.toEntity())
    }

    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        actionLogDao.clearLogs()
    }
}
