package com.quangthe.nhatky.data.mapper

import com.quangthe.nhatky.data.entities.DiaryEntity
import com.quangthe.nhatky.data.entities.PhotoUriEntity
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.models.Location
import com.quangthe.nhatky.models.PhotoUri

fun DiaryEntity.toDomain(photoUris: List<PhotoUriEntity>): Diary {
    val diary = Diary()
    diary.sequence = this.sequence
    diary.currentTimeMillis = this.currentTimeMillis
    diary.title = this.title
    diary.contents = this.contents
    diary.dateString = this.dateString
    diary.isAllDay = this.isAllDay
    diary.isEncrypt = this.isEncrypt
    diary.encryptKeyHash = this.encryptKeyHash
    diary.isHoliday = this.isHoliday
    diary.location = Location(this.address, this.latitude, this.longitude)
    
    val list = mutableListOf<PhotoUri>()
    photoUris.forEach { 
        list.add(PhotoUri(it.photoUri ?: "", it.mimeType ?: ""))
    }
    diary.photoUris = list
    return diary
}

fun Diary.toEntity(): DiaryEntity {
    return DiaryEntity(
        sequence = if (this.sequence == 0) 0 else this.sequence,
        currentTimeMillis = this.currentTimeMillis,
        title = this.title,
        contents = this.contents,
        dateString = this.dateString,
        isAllDay = this.isAllDay,
        isEncrypt = this.isEncrypt,
        encryptKeyHash = this.encryptKeyHash,
        isHoliday = this.isHoliday,
        address = this.location?.address,
        latitude = this.location?.latitude ?: 0.0,
        longitude = this.location?.longitude ?: 0.0
    )
}

fun PhotoUri.toEntity(diarySequence: Int): PhotoUriEntity {
    return PhotoUriEntity(
        diarySequence = diarySequence,
        photoUri = this.photoUri,
        mimeType = this.mimeType
    )
}
