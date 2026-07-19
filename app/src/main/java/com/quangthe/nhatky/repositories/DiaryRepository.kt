package com.quangthe.nhatky.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.quangthe.nhatky.data.AppDatabase
import com.quangthe.nhatky.data.mapper.toDomain
import com.quangthe.nhatky.data.mapper.toEntity
import com.quangthe.nhatky.helper.EasyDiaryApplication
import com.quangthe.nhatky.models.Diary

class DiaryRepository {
    private val database = AppDatabase.getInstance(EasyDiaryApplication.context!!)
    private val diaryDao = database.diaryDao()

    suspend fun findDiary(
        query: String? = null,
        entryType: Int = -1,
        checkFutureDiaryOption: Boolean = false,
    ): List<Diary> = withContext(Dispatchers.IO) {
        val diariesWithPhotos = diaryDao.getAllDiariesSync()
        diariesWithPhotos.filter { dwp ->
            val d = dwp.diary
            val matchesQuery = query.isNullOrEmpty() || 
                    d.title?.contains(query, ignoreCase = true) == true || 
                    d.contents?.contains(query, ignoreCase = true) == true
            matchesQuery
        }.map { it.diary.toDomain(it.photoUris) }
    }

    suspend fun findDiaryBy(sequence: Int): Diary? = withContext(Dispatchers.IO) {
        diaryDao.getDiaryWithPhotosBySequence(sequence)?.let { 
            it.diary.toDomain(it.photoUris)
        }
    }

    suspend fun insertDiary(diary: Diary) = withContext(Dispatchers.IO) {
        val sequence = diaryDao.insertDiary(diary.toEntity()).toInt()
        diary.photoUris?.let { photoUris ->
            val photoEntities = photoUris.map { 
                com.quangthe.nhatky.data.entities.PhotoUriEntity(
                    diarySequence = sequence,
                    photoUri = it.photoUri,
                    mimeType = it.mimeType
                )
            }
            diaryDao.insertPhotoUris(photoEntities)
        }
    }

    suspend fun updateDiary(diary: Diary) = withContext(Dispatchers.IO) {
        diaryDao.updateDiary(diary.toEntity())
        diaryDao.deletePhotoUrisByDiary(diary.sequence)
        diary.photoUris?.let { photoUris ->
            val photoEntities = photoUris.map { 
                com.quangthe.nhatky.data.entities.PhotoUriEntity(
                    diarySequence = diary.sequence,
                    photoUri = it.photoUri,
                    mimeType = it.mimeType
                )
            }
            diaryDao.insertPhotoUris(photoEntities)
        }
    }

    suspend fun countDiaryBy(dateString: String): Int = withContext(Dispatchers.IO) {
        diaryDao.getAllDiariesSync().count { it.diary.dateString == dateString }
    }

    suspend fun forceRefresh() = withContext(Dispatchers.IO) {
        // No-op
    }

    suspend fun findDiaryByDateString(dateString: String?, ascending: Boolean = false): List<Diary> = withContext(Dispatchers.IO) {
        val diariesWithPhotos = diaryDao.getAllDiariesSync()
        diariesWithPhotos.filter { it.diary.dateString == dateString }
            .map { it.diary.toDomain(it.photoUris) }
            .let { list ->
                if (ascending) list.sortedBy { it.currentTimeMillis }
                else list.sortedByDescending { it.currentTimeMillis }
            }
    }

    suspend fun findDiary(
        query: String?,
        caseSensitive: Boolean,
        startMillis: Long,
        endMillis: Long,
    ): List<Diary> = withContext(Dispatchers.IO) {
        val diariesWithPhotos = diaryDao.getAllDiariesSync()
        diariesWithPhotos.filter { dwp ->
            val d = dwp.diary
            val matchesQuery = query.isNullOrEmpty() || 
                    d.title?.contains(query, ignoreCase = caseSensitive.not()) == true || 
                    d.contents?.contains(query, ignoreCase = caseSensitive.not()) == true
            
            val matchesTime = (startMillis == 0L || d.currentTimeMillis >= startMillis) &&
                    (endMillis == 0L || d.currentTimeMillis <= endMillis)
            
            matchesQuery && matchesTime
        }.map { it.diary.toDomain(it.photoUris) }
    }

    suspend fun findDiaryByPhotoUri(photoUri: String): Diary? = withContext(Dispatchers.IO) {
        diaryDao.findDiaryByPhotoUri(photoUri)?.let { 
            it.diary.toDomain(it.photoUris)
        }
    }

    suspend fun findOldestDiary(): Diary? = withContext(Dispatchers.IO) {
        diaryDao.getOldestDiary()?.toDomain(emptyList())
    }

    suspend fun countDiaryAll(): Int = withContext(Dispatchers.IO) {
        diaryDao.getDiaryCount()
    }

    suspend fun getAllPhotoUris(): List<com.quangthe.nhatky.models.PhotoUri> = withContext(Dispatchers.IO) {
        diaryDao.getAllPhotoUris().map { 
            com.quangthe.nhatky.models.PhotoUri(it.photoUri ?: "", it.mimeType ?: "")
        }
    }

    suspend fun deleteDiary(sequence: Int) = withContext(Dispatchers.IO) {
        diaryDao.deleteDiary(sequence)
    }
}
