package com.quangthe.nhatky.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.core.manager.MediaManager

class SettingsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    val config = application.config

    /***************************************************************************************************
     *   Switch
     *
     ***************************************************************************************************/
    private val _enableCardViewPolicy: MutableStateFlow<Boolean> = MutableStateFlow(config.enableCardViewPolicy)
    val enableCardViewPolicy: StateFlow<Boolean> = _enableCardViewPolicy.asStateFlow()

    fun setEnableCardViewPolicy(isOn: Boolean) {
        _enableCardViewPolicy.value = isOn
    }

    private val _enableLocationInfo: MutableStateFlow<Boolean> = MutableStateFlow(config.enableLocationInfo)
    val enableLocationInfo: StateFlow<Boolean> get() = _enableLocationInfo.asStateFlow()

    fun setEnableLocationInfo(isOn: Boolean) {
        _enableLocationInfo.value = isOn
    }

    private val _fontFamily: MutableStateFlow<FontFamily?> = MutableStateFlow(null)
    val fontFamily: StateFlow<FontFamily?> get() = _fontFamily.asStateFlow()

    fun setFontFamily(fontFamily: FontFamily?) {
        _fontFamily.value = fontFamily
    }

    /***************************************************************************************************
     *   SubDescription
     *
     ***************************************************************************************************/
    private val _thumbnailSizeSubDescription: MutableStateFlow<String> = MutableStateFlow("${config.settingThumbnailSize}dp x ${config.settingThumbnailSize}dp")
    val thumbnailSizeSubDescription: StateFlow<String> = _thumbnailSizeSubDescription.asStateFlow()

    private val _datetimeFormatSubDescription: MutableStateFlow<String> =
        MutableStateFlow(
            DateUtils.getDateTimeStringForceFormatting(
                System.currentTimeMillis(),
                application,
            ),
        )
    val datetimeFormatSubDescription: StateFlow<String> = _datetimeFormatSubDescription.asStateFlow()

    private val _summaryMaxLinesSubDescription: MutableStateFlow<String> = MutableStateFlow(application.getString(R.string.max_lines_value, config.summaryMaxLines))
    val summaryMaxLinesSubDescription: StateFlow<String> get() = _summaryMaxLinesSubDescription.asStateFlow()

    private val _lineSpacingScaleFactor: MutableStateFlow<Float> = MutableStateFlow(config.lineSpacingScaleFactor)
    val lineSpacingScaleFactor: StateFlow<Float> = _lineSpacingScaleFactor.asStateFlow()

    fun setLineSpacingScaleFactor(value: Float) {
        _lineSpacingScaleFactor.value = value
    }

    private val _fontSize: MutableStateFlow<Float> = MutableStateFlow(config.settingFontSize)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    fun setFontSize(value: Float) {
        _fontSize.value = value
    }

    /***************************************************************************************************
     *   Media Cleanup
     *
     ***************************************************************************************************/
    suspend fun getOrphanedFilesCount(): Int {
        return MediaManager.getOrphanedFilesCount(getApplication())
    }

    suspend fun cleanupOrphanedFiles(): Int {
        return MediaManager.cleanupOrphanedFiles(getApplication())
    }
}
