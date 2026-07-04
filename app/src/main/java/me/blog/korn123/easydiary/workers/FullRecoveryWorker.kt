package me.blog.korn123.easydiary.workers

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.simplemobiletools.commons.helpers.BACKGROUND_COLOR
import com.simplemobiletools.commons.helpers.PRIMARY_COLOR
import com.simplemobiletools.commons.helpers.SETTING_CARD_VIEW_BACKGROUND_COLOR
import com.simplemobiletools.commons.helpers.TEXT_COLOR
import me.blog.korn123.commons.utils.EasyDiaryUtils
import me.blog.korn123.commons.utils.EasyDiaryUtils.jsonFileToHashMap
import me.blog.korn123.commons.utils.FontUtils
import me.blog.korn123.easydiary.extensions.config
import me.blog.korn123.easydiary.helper.*
import java.io.File

class FullRecoveryWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {
    private val mZipHelper = ZipHelper(context)

    override fun doWork(): Result {
        val uri = Uri.parse(inputData.getString(WorkerConstants.URI_STRING))
        mZipHelper.decompress(uri)
        if (mZipHelper.isOnProgress) {
            val jsonFilename = EasyDiaryUtils.getApplicationDataDirectory(context) + WORKING_DIRECTORY + "preference.json"
            if (File(jsonFilename).exists()) {
                val map = jsonFileToHashMap(jsonFilename)
                context.config.run {
                    // Settings Basic
                    (map[PRIMARY_COLOR] as? Double)?.let { primaryColor = it.toInt() }
                    (map[BACKGROUND_COLOR] as? Double)?.let { backgroundColor = it.toInt() }
                    (map[SETTING_CARD_VIEW_BACKGROUND_COLOR] as? Double)?.let { screenBackgroundColor = it.toInt() }
                    (map[TEXT_COLOR] as? Double)?.let { textColor = it.toInt() }
                    (map[SETTING_THUMBNAIL_SIZE] as? Double)?.let { settingThumbnailSize = it.toFloat() }
                    (map[SETTING_CONTENTS_SUMMARY] as? Boolean)?.let { enableContentsSummary = it }
                    (map[SETTING_SUMMARY_MAX_LINES] as? Double)?.let { summaryMaxLines = it.toInt() }
                    (map[ENABLE_CARD_VIEW_POLICY] as? Boolean)?.let { enableCardViewPolicy = it }
//                    (map[SETTING_MULTIPLE_PICKER] as? Boolean)?.let { multiPickerEnable = it }
                    (map[DIARY_SEARCH_QUERY_CASE_SENSITIVE] as? Boolean)?.let { diarySearchQueryCaseSensitive = it }
                    (map[SETTING_CALENDAR_START_DAY] as? Double)?.let { calendarStartDay = it.toInt() }
                    (map[SETTING_CALENDAR_SORTING] as? Double)?.let { calendarSorting = it.toInt() }
                    (map[SETTING_COUNT_CHARACTERS] as? Boolean)?.let { enableCountCharacters = it }
                    (map[HOLD_POSITION_ENTER_EDIT_SCREEN] as? Boolean)?.let { holdPositionEnterEditScreen = it }

                    // Settings font
                    (map[SETTING_FONT_NAME] as? String)?.let { settingFontName = it }
                    (map[LINE_SPACING_SCALE_FACTOR] as? Double)?.let { lineSpacingScaleFactor = it.toFloat() }
                    (map[SETTING_FONT_SIZE] as? Double)?.let { settingFontSize = it.toFloat() }
                    (map[SETTING_CALENDAR_FONT_SCALE] as? Double)?.let { settingCalendarFontScale = it.toFloat() }
                    (map[SETTING_BOLD_STYLE] as? Boolean)?.let { boldStyleEnable = it }

                    // Settings Lock

                    // ETC.
                    (map[SETTING_SELECTED_SYMBOLS] as? String)?.let { selectedSymbols = it }

                    updatePreference = true
                }
            }
            FontUtils.setCommonTypeface(context)
            mZipHelper.updateNotification(NOTIFICATION_DECOMPRESS_ID, "Import complete", "You can now select a restore point using the Restore Diary feature.")
        } else {
            NotificationManagerCompat.from(applicationContext).cancel(NOTIFICATION_DECOMPRESS_ID)
        }
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        mZipHelper.isOnProgress = false
        NotificationManagerCompat.from(applicationContext).cancel(NOTIFICATION_DECOMPRESS_ID)
    }
}
