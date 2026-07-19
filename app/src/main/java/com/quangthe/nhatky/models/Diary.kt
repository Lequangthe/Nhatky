package com.quangthe.nhatky.models

import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.helper.DateUtilConstants
import com.quangthe.nhatky.helper.DiaryEditingConstants
import com.quangthe.nhatky.ui.models.DiaryUiModel

/**
 * Created by CHO HANJOONG on 2017-03-16.
 */

data class Diary(
    var sequence: Int = DiaryEditingConstants.DIARY_SEQUENCE_INIT,
    var currentTimeMillis: Long = System.currentTimeMillis(),
    var title: String? = null,
    var contents: String? = null,
    var dateString: String? = null,
    var photoUris: MutableList<PhotoUri>? = mutableListOf(),
    var isAllDay: Boolean = false,
    var isEncrypt: Boolean = false,
    var encryptKeyHash: String? = null,
    var isSelected: Boolean = false,
    var location: Location? = null,
    var isHoliday: Boolean = false,
    
    @Deprecated("Redundant after Phase 2 cleanup")
    var originSequence: Int = DiaryEditingConstants.DIARY_ORIGIN_SEQUENCE_INIT,
    @Deprecated("Legacy font system")
    var fontName: String? = null,
    @Deprecated("Legacy font system")
    var fontSize: Float = 0f,
    @Deprecated("Use separate Note/Task models instead")
    var entryType: Int = 0
) {
    init {
        if (dateString == null) {
            updateDateString()
        }
    }

    fun updateDateString() {
        this.dateString = DateUtils.timeMillisToDateTime(this.currentTimeMillis, DateUtilConstants.DATE_PATTERN_DASH)
    }

    fun photoUrisWithEncryptionPolicy(): List<PhotoUri>? =
        when (isEncrypt) {
            true -> {
                photoUris?.map { PhotoUri("") }
            }

            false -> {
                photoUris
            }
        }

    fun toUiModel() =
        DiaryUiModel(
            sequence = sequence,
            title = title.orEmpty(),
            contents = contents.orEmpty(),
            dateString = dateString.orEmpty(),
            currentTimeMillis = currentTimeMillis,
            isAllDay = isAllDay,
        )
}
