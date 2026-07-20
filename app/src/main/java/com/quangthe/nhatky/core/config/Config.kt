package com.quangthe.nhatky.core.config

import android.content.Context
import android.graphics.Color
import android.text.format.DateFormat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.simplemobiletools.commons.extensions.getSharedPrefs
import com.simplemobiletools.commons.helpers.*
import com.quangthe.nhatky.R
import com.quangthe.nhatky.enums.DateTimeFormat
import com.quangthe.nhatky.extensions.dpToPixelFloatValue
import java.util.*

class Config(val context: Context) {
    private val legacyPrefs = PreferenceManager.getDefaultSharedPreferences(context)!!
    private val prefs = context.getSharedPrefs()

    var textColor: Int
        get() = prefs.getInt(TEXT_COLOR, ContextCompat.getColor(context, R.color.default_text_color))
        set(textColor) = prefs.edit().putInt(TEXT_COLOR, textColor).apply()

    var backgroundColor: Int
        get() = prefs.getInt(BACKGROUND_COLOR, ContextCompat.getColor(context, R.color.default_background_color))
        set(backgroundColor) = prefs.edit().putInt(BACKGROUND_COLOR, backgroundColor).apply()

    var primaryColor: Int
        get() = prefs.getInt(PRIMARY_COLOR, ContextCompat.getColor(context, R.color.color_primary))
        set(primaryColor) = prefs.edit().putInt(PRIMARY_COLOR, primaryColor).apply()

    var use24HourFormat: Boolean
        get() = prefs.getBoolean(USE_24_HOUR_FORMAT, DateFormat.is24HourFormat(context))
        set(use24HourFormat) = prefs.edit().putBoolean(USE_24_HOUR_FORMAT, use24HourFormat).apply()

    var isSundayFirst: Boolean
        get() {
            val isSundayFirst = Calendar.getInstance(Locale.getDefault()).firstDayOfWeek == Calendar.SUNDAY
            return prefs.getBoolean(SUNDAY_FIRST, isSundayFirst)
        }
        set(sundayFirst) = prefs.edit().putBoolean(SUNDAY_FIRST, sundayFirst).apply()

    var screenBackgroundColor: Int
        get() = prefs.getInt(SETTING_CARD_VIEW_BACKGROUND_COLOR, Color.parseColor(EASYDIARY_THEME_SCREEN_BACKGROUND_COLOR))
        set(screenBackgroundColor) = prefs.edit().putInt(SETTING_CARD_VIEW_BACKGROUND_COLOR, screenBackgroundColor).apply()


    /// ------------------------------------------------------------------
    /// Awesome Application Factory legacy properties
    /// ------------------------------------------------------------------
    var aafPinLockPauseMillis: Long
        get() = prefs.getLong(AAF_PIN_LOCK_PAUSE_MILLIS, 0L)
        set(aafPinLockPauseMillis) = prefs.edit().putLong(AAF_PIN_LOCK_PAUSE_MILLIS, aafPinLockPauseMillis).apply()

    var isThemeChanged: Boolean
        get() = prefs.getBoolean(AAF_THEME_CHANGE, false)
        set(isThemeChanged) = prefs.edit().putBoolean(AAF_THEME_CHANGE, isThemeChanged).apply()


    /// ------------------------------------------------------------------
    /// Dev properties
    /// ------------------------------------------------------------------
    /// ------------------------------------------------------------------
    /// Easy Diary properties
    /// ------------------------------------------------------------------
    var aafPinLockSavedPassword: String
        get() = legacyPrefs.getString(APP_LOCK_SAVED_PASSWORD, APP_LOCK_DEFAULT_PASSWORD)!!
        set(aafPinLockSavedPassword) = legacyPrefs.edit().putString(APP_LOCK_SAVED_PASSWORD, aafPinLockSavedPassword).apply()

    var previousActivity: Int
        get() = legacyPrefs.getInt(PREVIOUS_ACTIVITY, -1)
        set(previousActivity) = legacyPrefs.edit().putInt(PREVIOUS_ACTIVITY, previousActivity).apply()

    var settingFontSize: Float
        get() = legacyPrefs.getFloat(SETTING_FONT_SIZE, context.dpToPixelFloatValue(SUPPORT_LANGUAGE_FONT_SIZE_DEFAULT_SP.toFloat()))
        set(settingFontSize) = legacyPrefs.edit().putFloat(SETTING_FONT_SIZE, settingFontSize).apply()

    var settingCalendarFontScale: Float
        get() = legacyPrefs.getFloat(SETTING_CALENDAR_FONT_SCALE, DEFAULT_CALENDAR_FONT_SCALE)
        set(settingCalendarFontScale) = legacyPrefs.edit().putFloat(SETTING_CALENDAR_FONT_SCALE, settingCalendarFontScale).apply()

    var settingFontName: String
        get() = legacyPrefs.getString(SETTING_FONT_NAME, "")!!
        set(settingFontName) = legacyPrefs.edit().putString(SETTING_FONT_NAME, settingFontName).apply()

    var aafPinLockEnable: Boolean
        get() = legacyPrefs.getBoolean(APP_LOCK_ENABLE, false)
        set(aafPinLockEnable) = legacyPrefs.edit().putBoolean(APP_LOCK_ENABLE, aafPinLockEnable).apply()

    var isInitDummyData: Boolean
        get() = legacyPrefs.getBoolean(INIT_DUMMY_DATA_FLAG, false)
        set(isInitDummyData) = legacyPrefs.edit().putBoolean(INIT_DUMMY_DATA_FLAG, isInitDummyData).apply()

    var lineSpacingScaleFactor: Float
        get() = legacyPrefs.getFloat(LINE_SPACING_SCALE_FACTOR, LINE_SPACING_SCALE_DEFAULT)
        set(lineSpacingScaleFactor) = legacyPrefs.edit().putFloat(LINE_SPACING_SCALE_FACTOR, lineSpacingScaleFactor).apply()

    var settingThumbnailSize: Float
        get() = prefs.getFloat(SETTING_THUMBNAIL_SIZE, THUMBNAIL_SIZE_DEFAULT_DP.toFloat())
        set(settingThumbnailSize) = prefs.edit().putFloat(SETTING_THUMBNAIL_SIZE, settingThumbnailSize).apply()

    var boldStyleEnable: Boolean
        get() = prefs.getBoolean(SETTING_BOLD_STYLE, false)
        set(boldStyleEnable) = prefs.edit().putBoolean(SETTING_BOLD_STYLE, boldStyleEnable).apply()

    var fingerprintLockEnable: Boolean
        get() = prefs.getBoolean(SETTING_FINGERPRINT_LOCK, false)
        set(fingerprintLockEnable) = prefs.edit().putBoolean(SETTING_FINGERPRINT_LOCK, fingerprintLockEnable).apply()

    var fingerprintEncryptData: String
        get() = prefs.getString(FINGERPRINT_ENCRYPT_DATA, "") ?: ""
        set(fingerprintEncryptData) = prefs.edit().putString(FINGERPRINT_ENCRYPT_DATA, fingerprintEncryptData).apply()

    var fingerprintEncryptDataIV: String
        get() = prefs.getString(FINGERPRINT_ENCRYPT_DATA_IV, "") ?: ""
        set(fingerprintEncryptDataIV) = prefs.edit().putString(FINGERPRINT_ENCRYPT_DATA_IV, fingerprintEncryptDataIV).apply()

    var fingerprintAuthenticationFailCount: Int
        get() = prefs.getInt(FINGERPRINT_AUTHENTICATION_FAIL_COUNT, 0)
        set(fingerprintAuthenticationFailCount) = prefs.edit().putInt(FINGERPRINT_AUTHENTICATION_FAIL_COUNT, fingerprintAuthenticationFailCount).apply()

    var enableCardViewPolicy: Boolean
        get() = prefs.getBoolean(ENABLE_CARD_VIEW_POLICY, true)
        set(enableCardViewPolicy) = prefs.edit().putBoolean(ENABLE_CARD_VIEW_POLICY, enableCardViewPolicy).apply()

    var enableContentsSummary: Boolean
        get() = prefs.getBoolean(SETTING_CONTENTS_SUMMARY, true)
        set(enableContentsSummary) = prefs.edit().putBoolean(SETTING_CONTENTS_SUMMARY, enableContentsSummary).apply()

    var clearLegacyToken: Boolean
        get() = prefs.getBoolean(SETTING_CLEAR_LEGACY_TOKEN, false)
        set(clearLegacyToken) = prefs.edit().putBoolean(SETTING_CLEAR_LEGACY_TOKEN, clearLegacyToken).apply()

    var calendarStartDay: Int
        get() = prefs.getInt(SETTING_CALENDAR_START_DAY, CALENDAR_START_DAY_SUNDAY)
        set(calendarStartDay) = prefs.edit().putInt(SETTING_CALENDAR_START_DAY, calendarStartDay).apply()

    var diaryBackupLocal: Long
        get() = prefs.getLong(DIARY_LAST_BACKUP_TIMESTAMP_LOCAL, 0)
        set(diaryBackupLocal) = prefs.edit().putLong(DIARY_LAST_BACKUP_TIMESTAMP_LOCAL, diaryBackupLocal).apply()

    var calendarSorting: Int
        get() = prefs.getInt(SETTING_CALENDAR_SORTING, CALENDAR_SORTING_DESC)
        set(calendarSorting) = prefs.edit().putInt(SETTING_CALENDAR_SORTING, calendarSorting).apply()

    var updatePreference: Boolean
        get() = prefs.getBoolean(UPDATE_SHARED_PREFERENCE, false)
        set(updatePreference) = prefs.edit().putBoolean(UPDATE_SHARED_PREFERENCE, updatePreference).apply()

    var holdPositionEnterEditScreen: Boolean
        get() = prefs.getBoolean(HOLD_POSITION_ENTER_EDIT_SCREEN, false)
        set(holdPositionEnterEditScreen) = prefs.edit().putBoolean(HOLD_POSITION_ENTER_EDIT_SCREEN, holdPositionEnterEditScreen).apply()

    var diarySearchQueryCaseSensitive: Boolean
        get() = legacyPrefs.getBoolean(DIARY_SEARCH_QUERY_CASE_SENSITIVE, false)
        set(diarySearchQueryCaseSensitive) = legacyPrefs.edit().putBoolean(DIARY_SEARCH_QUERY_CASE_SENSITIVE, diarySearchQueryCaseSensitive).apply()

    var summaryMaxLines: Int
        get() = prefs.getInt(SETTING_SUMMARY_MAX_LINES, SETTING_SUMMARY_MAX_LINES_DEFAULT)
        set(summaryMaxLines) = prefs.edit().putInt(SETTING_SUMMARY_MAX_LINES, summaryMaxLines).apply()

    var enableLocationInfo: Boolean
        get() = prefs.getBoolean(SETTING_LOCATION_INFO, false)
        set(enableLocationInfo) = prefs.edit().putBoolean(SETTING_LOCATION_INFO, enableLocationInfo).apply()

    var appExecutionCount: Int
        get() = prefs.getInt(APP_EXECUTION_COUNT, APP_EXECUTION_COUNT_DEFAULT)
        set(appExecutionCount) = prefs.edit().putInt(APP_EXECUTION_COUNT, appExecutionCount).apply()

    var diaryMainSpanCountLandscape: Int
        get() = prefs.getInt(SETTING_DIARY_MAIN_SPAN_COUNT_LANDSCAPE, 1)
        set(diaryMainSpanCountLandscape) = prefs.edit().putInt(SETTING_DIARY_MAIN_SPAN_COUNT_LANDSCAPE, diaryMainSpanCountLandscape).apply()

    var diaryMainSpanCountPortrait: Int
        get() = prefs.getInt(SETTING_DIARY_MAIN_SPAN_COUNT_PORTRAIT, 1)
        set(diaryMainSpanCountPortrait) = prefs.edit().putInt(SETTING_DIARY_MAIN_SPAN_COUNT_PORTRAIT, diaryMainSpanCountPortrait).apply()

    var diaryViewMode: Int
        get() = prefs.getInt(DIARY_MODE, 0)
        set(diaryViewMode) = prefs.edit().putInt(DIARY_MODE, diaryViewMode).apply()

    var enableStatusBarDarkenColor: Boolean
        get() = prefs.getBoolean(SETTING_ENABLE_STATUSBAR_DARKEN_COLOR, true)
        set(enableStatusBarDarkenColor) = prefs.edit().putBoolean(SETTING_ENABLE_STATUSBAR_DARKEN_COLOR, enableStatusBarDarkenColor).apply()

    var enableDDayFlexboxLayout: Boolean
        get() = prefs.getBoolean(SETTING_ENABLE_DDAY_FLEXBOX_LAYOUT, false)
        set(enableDDayFlexboxLayout) = prefs.edit().putBoolean(SETTING_ENABLE_DDAY_FLEXBOX_LAYOUT, enableDDayFlexboxLayout).apply()

    var enableDashboardCalendar: Boolean
        get() = prefs.getBoolean(SETTING_ENABLE_DASHBOARD_CALENDAR, true)
        set(enableDashboardCalendar) = prefs.edit().putBoolean(SETTING_ENABLE_DASHBOARD_CALENDAR, enableDashboardCalendar).apply()

    var settingDatetimeFormat: String
        get() = prefs.getString(SETTING_DATETIME_FORMAT, DateTimeFormat.DATE_FULL_AND_TIME_SHORT.toString())!!
        set(settingDatetimeFormat) = prefs.edit().putString(SETTING_DATETIME_FORMAT, settingDatetimeFormat).apply()

    var enableMarkdown: Boolean
        get() = prefs.getBoolean(SETTING_ENABLE_MARKDOWN, false)
        set(enableMarkdown) = prefs.edit().putBoolean(SETTING_ENABLE_MARKDOWN, enableMarkdown).apply()

    var enableDebugOptionToastLocation: Boolean
        get() = prefs.getBoolean(SETTING_DEBUG_OPTION_TOAST_LOCATION, false)
        set(enableDebugOptionToastLocation) = prefs.edit().putBoolean(SETTING_DEBUG_OPTION_TOAST_LOCATION, enableDebugOptionToastLocation).apply()

    var enableDebugOptionToastAttachedPhoto: Boolean
        get() = prefs.getBoolean(SETTING_DEBUG_OPTION_TOAST_ATTACHED_PHOTO, false)
        set(enableDebugOptionToastAttachedPhoto) = prefs.edit().putBoolean(SETTING_DEBUG_OPTION_TOAST_ATTACHED_PHOTO, enableDebugOptionToastAttachedPhoto).apply()

    var enableDebugOptionToastNotificationInfo: Boolean
        get() = prefs.getBoolean(SETTING_DEBUG_OPTION_TOAST_NOTIFICATION_INFO, false)
        set(enableDebugOptionToastNotificationInfo) = prefs.edit().putBoolean(SETTING_DEBUG_OPTION_TOAST_NOTIFICATION_INFO, enableDebugOptionToastNotificationInfo).apply()

    var enableDebugOptionVisibleDiarySequence: Boolean
        get() = prefs.getBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_DIARY_SEQUENCE, false)
        set(enableDebugOptionVisibleDiarySequence) = prefs.edit().putBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_DIARY_SEQUENCE, enableDebugOptionVisibleDiarySequence).apply()

    var enableDebugOptionVisibleTreeStatus: Boolean
        get() = prefs.getBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_TREE_STATUS, false)
        set(enableDebugOptionVisibleTreeStatus) = prefs.edit().putBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_TREE_STATUS, enableDebugOptionVisibleTreeStatus).apply()

    var enableDebugOptionVisibleFontPreviewEmoji: Boolean
        get() = prefs.getBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_FONT_PREVIEW_EMOJI, false)
        set(enableDebugOptionVisibleFontPreviewEmoji) = prefs.edit().putBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_FONT_PREVIEW_EMOJI, enableDebugOptionVisibleFontPreviewEmoji).apply()

    var enableDebugOptionVisibleTemporaryDiary: Boolean
        get() = prefs.getBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_TEMPORARY_DIARY, false)
        set(enableDebugOptionVisibleTemporaryDiary) = prefs.edit().putBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_TEMPORARY_DIARY, enableDebugOptionVisibleTemporaryDiary).apply()

    var gallerySpanCountLandscape: Int
        get() = prefs.getInt(SETTING_GALLERY_SPAN_COUNT_LANDSCAPE, 5)
        set(gallerySpanCountLandscape) = prefs.edit().putInt(SETTING_GALLERY_SPAN_COUNT_LANDSCAPE, gallerySpanCountLandscape).apply()

    var gallerySpanCountPortrait: Int
        get() = prefs.getInt(SETTING_GALLERY_SPAN_COUNT_PORTRAIT, 2)
        set(gallerySpanCountPortrait) = prefs.edit().putInt(SETTING_GALLERY_SPAN_COUNT_PORTRAIT, gallerySpanCountPortrait).apply()

    var visibleUnlinkedPhotos: Boolean
        get() = prefs.getBoolean(SETTING_VISIBLE_UNLINKED_PHOTOS, false)
        set(visibleUnlinkedPhotos) = prefs.edit().putBoolean(SETTING_VISIBLE_UNLINKED_PHOTOS, visibleUnlinkedPhotos).apply()

    var disableFutureDiary: Boolean
        get() = prefs.getBoolean(SETTING_DISABLE_FUTURE_DIARY, false)
        set(disableFutureDiary) = prefs.edit().putBoolean(SETTING_DISABLE_FUTURE_DIARY, disableFutureDiary).apply()

    var enableCountCharacters: Boolean
        get() = prefs.getBoolean(SETTING_COUNT_CHARACTERS, false)
        set(enableCountCharacters) = prefs.edit().putBoolean(SETTING_COUNT_CHARACTERS, enableCountCharacters).apply()

    var enableDebugOptionVisibleAlarmSequence: Boolean
        get() = prefs.getBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_ALARM_SEQUENCE, false)
        set(enableDebugOptionVisibleAlarmSequence) = prefs.edit().putBoolean(SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_ALARM_SEQUENCE, enableDebugOptionVisibleAlarmSequence).apply()

    companion object {
        fun newInstance(context: Context) = Config(context)
    }
}
