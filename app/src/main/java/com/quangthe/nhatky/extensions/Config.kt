package com.quangthe.nhatky.extensions

import android.content.Context
import android.preference.PreferenceManager
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils.hashMapToJsonString
import com.quangthe.nhatky.core.config.APP_LOCK_ENABLE
import com.quangthe.nhatky.core.config.APP_LOCK_SAVED_PASSWORD
import com.quangthe.nhatky.core.config.Config
import com.quangthe.nhatky.core.config.DIARY_SEARCH_QUERY_CASE_SENSITIVE
import com.quangthe.nhatky.core.config.ENABLE_CARD_VIEW_POLICY
import com.quangthe.nhatky.core.config.HOLD_POSITION_ENTER_EDIT_SCREEN
import com.quangthe.nhatky.core.config.LINE_SPACING_SCALE_FACTOR
import com.quangthe.nhatky.core.config.SETTING_BOLD_STYLE
import com.quangthe.nhatky.core.config.SETTING_CALENDAR_FONT_SCALE
import com.quangthe.nhatky.core.config.SETTING_CALENDAR_SORTING
import com.quangthe.nhatky.core.config.SETTING_CALENDAR_START_DAY
import com.quangthe.nhatky.core.config.SETTING_CONTENTS_SUMMARY
import com.quangthe.nhatky.core.config.SETTING_COUNT_CHARACTERS
import com.quangthe.nhatky.core.config.SETTING_FONT_NAME
import com.quangthe.nhatky.core.config.SETTING_FONT_SIZE
import com.quangthe.nhatky.core.config.SETTING_SUMMARY_MAX_LINES
import com.quangthe.nhatky.core.config.SETTING_THUMBNAIL_SIZE
import com.quangthe.nhatky.enums.DateTimeFormat
import com.simplemobiletools.commons.helpers.BACKGROUND_COLOR
import com.simplemobiletools.commons.helpers.PRIMARY_COLOR
import com.simplemobiletools.commons.helpers.SETTING_CARD_VIEW_BACKGROUND_COLOR
import com.simplemobiletools.commons.helpers.TEXT_COLOR

val Context.config: Config get() = Config.newInstance(this)

fun Context.preferencesContains(key: String): Boolean {
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    return preferences.contains(key)
}

fun Context.preferenceToJsonString(): String {
    var jsonString: String = ""
    val preferenceMap: HashMap<String, Any> = hashMapOf()

    // Settings Basic
    preferenceMap[PRIMARY_COLOR] = config.primaryColor
    preferenceMap[BACKGROUND_COLOR] = config.backgroundColor
    preferenceMap[SETTING_CARD_VIEW_BACKGROUND_COLOR] = config.screenBackgroundColor
    preferenceMap[TEXT_COLOR] = config.textColor
    preferenceMap[SETTING_THUMBNAIL_SIZE] = config.settingThumbnailSize
    preferenceMap[SETTING_CONTENTS_SUMMARY] = config.enableContentsSummary
    preferenceMap[SETTING_SUMMARY_MAX_LINES] = config.summaryMaxLines
    preferenceMap[ENABLE_CARD_VIEW_POLICY] = config.enableCardViewPolicy
//    preferenceMap[SETTING_MULTIPLE_PICKER] = config.multiPickerEnable
    preferenceMap[DIARY_SEARCH_QUERY_CASE_SENSITIVE] = config.diarySearchQueryCaseSensitive
    preferenceMap[SETTING_CALENDAR_START_DAY] = config.calendarStartDay
    preferenceMap[SETTING_CALENDAR_SORTING] = config.calendarSorting
    preferenceMap[SETTING_COUNT_CHARACTERS] = config.enableCountCharacters
    preferenceMap[HOLD_POSITION_ENTER_EDIT_SCREEN] = config.holdPositionEnterEditScreen

    // Settings font
    preferenceMap[SETTING_FONT_NAME] = config.settingFontName
    preferenceMap[LINE_SPACING_SCALE_FACTOR] = config.lineSpacingScaleFactor
    preferenceMap[SETTING_FONT_SIZE] = config.settingFontSize
    preferenceMap[SETTING_CALENDAR_FONT_SCALE] = config.settingCalendarFontScale
    preferenceMap[SETTING_BOLD_STYLE] = config.boldStyleEnable

    // Settings Lock
    preferenceMap[APP_LOCK_ENABLE] = config.aafPinLockEnable
    preferenceMap[APP_LOCK_SAVED_PASSWORD] = config.aafPinLockSavedPassword

    return hashMapToJsonString(preferenceMap)
}

fun Context.storedDatetimeFormat() = DateTimeFormat.valueOf(config.settingDatetimeFormat)
