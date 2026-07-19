package com.quangthe.nhatky.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.quangthe.nhatky.data.entities.DiaryEntity
import com.quangthe.nhatky.data.entities.DiaryWithPhotos
import com.quangthe.nhatky.data.entities.PhotoUriEntity

@Dao
interface DiaryDao {
    @Transaction
    @Query("SELECT * FROM diaries ORDER BY currentTimeMillis DESC, sequence DESC")
    fun getAllDiariesWithPhotos(): Flow<List<DiaryWithPhotos>>

    @Query("SELECT * FROM diaries ORDER BY currentTimeMillis DESC, sequence DESC")
    suspend fun getAllDiariesSync(): List<DiaryWithPhotos>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: DiaryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoUris(photoUris: List<PhotoUriEntity>)

    @Query("DELETE FROM diaries WHERE sequence = :sequence")
    suspend fun deleteDiary(sequence: Int)

    @Update
    suspend fun updateDiary(diary: DiaryEntity)

    @Query("SELECT * FROM diaries WHERE sequence = :sequence")
    suspend fun getDiaryBySequence(sequence: Int): DiaryEntity?

    @Query("SELECT * FROM diaries WHERE sequence = :sequence")
    suspend fun getDiaryWithPhotosBySequence(sequence: Int): DiaryWithPhotos?

    @Query("DELETE FROM photo_uris WHERE diarySequence = :diarySequence")
    suspend fun deletePhotoUrisByDiary(diarySequence: Int)

    @Transaction
    @Query("SELECT * FROM diaries WHERE sequence IN (SELECT diarySequence FROM photo_uris WHERE photoUri LIKE '%' || :photoUri || '%') LIMIT 1")
    suspend fun findDiaryByPhotoUri(photoUri: String): DiaryWithPhotos?

    @Query("SELECT * FROM diaries ORDER BY currentTimeMillis ASC LIMIT 1")
    suspend fun getOldestDiary(): DiaryEntity?

    @Query("SELECT COUNT(*) FROM diaries")
    suspend fun getDiaryCount(): Int

    @Query("SELECT * FROM photo_uris")
    suspend fun getAllPhotoUris(): List<PhotoUriEntity>
}
