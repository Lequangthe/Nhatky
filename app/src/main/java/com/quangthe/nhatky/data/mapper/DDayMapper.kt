package com.quangthe.nhatky.data.mapper

import com.quangthe.nhatky.data.entities.DDayEntity
import com.quangthe.nhatky.models.DDay

fun DDayEntity.toDomain(): DDay {
    val dDay = DDay()
    dDay.sequence = this.sequence
    dDay.targetTimeStamp = this.targetTimeStamp
    dDay.title = this.title
    return dDay
}

fun DDay.toEntity(): DDayEntity {
    return DDayEntity(
        sequence = if (this.sequence == -1) 0 else this.sequence,
        targetTimeStamp = this.targetTimeStamp,
        title = this.title
    )
}
