package com.quangthe.nhatky.data.mapper

import com.quangthe.nhatky.data.entities.ActionLogEntity
import com.quangthe.nhatky.models.ActionLog

fun ActionLogEntity.toDomain(): ActionLog {
    val log = ActionLog()
    log.sequence = this.sequence
    log.className = this.className
    log.signature = this.signature
    log.key = this.key
    log.value = this.value
    return log
}

fun ActionLog.toEntity(): ActionLogEntity {
    return ActionLogEntity(
        sequence = this.sequence,
        className = this.className,
        signature = this.signature,
        key = this.key,
        value = this.value
    )
}
