package com.quangthe.nhatky.models

import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.enums.ActionLogKey
import java.text.SimpleDateFormat

data class ActionLog(
    var sequence: Int = 0,
    var className: String? = null,
    var signature: String? = null,
    var key: String? = null,
    var value: String? = null
) {
    constructor(className: String?, signature: String?, key: ActionLogKey = ActionLogKey.INFO, value: String?) : this(
        sequence = 0,
        className = "[${DateUtils.getDateTimeStringFromTimeMillis(System.currentTimeMillis(), SimpleDateFormat.FULL, SimpleDateFormat.MEDIUM)}] $className",
        signature = signature,
        key = key.name,
        value = value
    )
}
