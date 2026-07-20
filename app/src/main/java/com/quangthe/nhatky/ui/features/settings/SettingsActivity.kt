package com.quangthe.nhatky.ui.features.settings

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ListView
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.quangthe.nhatky.R
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import com.quangthe.nhatky.ui.features.main.DiaryMainActivity
import com.quangthe.nhatky.adapters.OptionItemAdapter
import com.quangthe.nhatky.enums.DateTimeFormat
import com.quangthe.nhatky.enums.DialogMode
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.core.config.*
import com.quangthe.nhatky.core.navigation.TransitionHelper
import com.quangthe.nhatky.core.manager.MediaManager
import com.quangthe.nhatky.core.export.ExportManager
import com.quangthe.nhatky.ui.components.*
import com.quangthe.nhatky.ui.theme.AppTheme
import com.quangthe.nhatky.ui.features.auth.PinLockActivity
import com.quangthe.nhatky.ui.features.auth.FingerprintLockActivity
import com.quangthe.nhatky.ui.features.settings.CustomizationActivity
import com.quangthe.nhatky.viewmodels.SettingsViewModel
import com.quangthe.nhatky.commons.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : EasyDiaryComposeBaseActivity() {
    private lateinit var mRequestLocationSourceLauncher: ActivityResultLauncher<Intent>

    private val exportTextLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            result.data?.data?.let { uri ->
                lifecycleScope.launch {
                    val success = ExportManager.exportToText(
                        this@SettingsActivity,
                        uri,
                        exportSelection["diary"] ?: false,
                        exportSelection["note"] ?: false,
                        exportSelection["task"] ?: false
                    )
                    if (success) {
                        makeSnackBar("Export successful!")
                    } else {
                        makeSnackBar("Export failed.")
                    }
                }
            }
        }
    }

    private val exportSelection = mutableMapOf("diary" to true, "note" to true, "task" to true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        mRequestLocationSourceLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                pauseLock()
                if (isLocationEnabled()) {
                    config.enableLocationInfo = true
                    mSettingsViewModel.setEnableLocationInfo(true)
                    makeSnackBar("GPS provider setting is activated!!!")
                } else {
                    makeSnackBar("The request operation did not complete normally.")
                }
            }

        setContent {
            AppTheme {
                SettingsScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    private fun SettingsScreen() {
        val configuration = LocalConfiguration.current
        val context = LocalContext.current
        val enableCardViewPolicy by mSettingsViewModel.enableCardViewPolicy.collectAsState()
        val fontSize by mSettingsViewModel.fontSize.collectAsState()
        val lineSpacingScaleFactor by mSettingsViewModel.lineSpacingScaleFactor.collectAsState()
        val fontFamily by mSettingsViewModel.fontFamily.collectAsState()
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings)) },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            CardContainer(modifier = Modifier.padding(padding)) {
                val cardModifier = Modifier.fillMaxWidth().then(
                    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) Modifier.fillMaxWidth(0.5f) else Modifier
                )
                
                // --- SECTION: BASIC SETTINGS ---
                CategoryTitleCard(title = stringResource(R.string.preferences_category_settings))
                FlowRow(maxItemsInEachRow = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 2) {
                    SimpleCard(
                        title = stringResource(R.string.setting_primary_color_title),
                        description = stringResource(R.string.setting_primary_color_summary),
                        modifier = cardModifier,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        TransitionHelper.startActivityWithTransition(
                            this@SettingsActivity,
                            Intent(this@SettingsActivity, CustomizationActivity::class.java),
                        )
                    }

                    var enableMarkdown by remember { mutableStateOf(config.enableMarkdown) }
                    SwitchCard(
                        title = stringResource(R.string.markdown_setting_title),
                        description = stringResource(R.string.markdown_setting_summary),
                        modifier = cardModifier,
                        isOn = enableMarkdown,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        enableMarkdown = !enableMarkdown
                        config.enableMarkdown = enableMarkdown
                    }

                    val enableLocationInfo by mSettingsViewModel.enableLocationInfo.collectAsState()
                    SwitchCard(
                        title = stringResource(R.string.location_info_title),
                        description = stringResource(R.string.location_info_description),
                        modifier = cardModifier,
                        isOn = enableLocationInfo,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        handleLocationToggle(!enableLocationInfo)
                    }

                    SimpleCard(
                        title = stringResource(R.string.thumbnail_setting_title),
                        description = stringResource(R.string.thumbnail_setting_summary),
                        subDescription = mSettingsViewModel.thumbnailSizeSubDescription.collectAsState().value,
                        modifier = cardModifier,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        openThumbnailSettingDialog()
                    }

                    SimpleCard(
                        title = stringResource(R.string.datetime_setting_title),
                        description = stringResource(R.string.datetime_setting_summary),
                        subDescription = mSettingsViewModel.datetimeFormatSubDescription.collectAsState().value,
                        modifier = cardModifier,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        openDatetimeFormattingSettingDialog()
                    }

                    var enableContentsSummary by remember { mutableStateOf(config.enableContentsSummary) }
                    SwitchCard(
                        title = stringResource(R.string.contents_summary_title),
                        description = stringResource(R.string.contents_summary_description),
                        modifier = cardModifier,
                        isOn = enableContentsSummary,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        enableContentsSummary = !enableContentsSummary
                        config.enableContentsSummary = enableContentsSummary
                    }

                    if (enableContentsSummary) {
                        SimpleCard(
                            title = stringResource(R.string.max_lines_title),
                            description = stringResource(R.string.max_lines_summary),
                            subDescription = mSettingsViewModel.summaryMaxLinesSubDescription.collectAsState().value,
                            modifier = cardModifier,
                            enableCardViewPolicy = enableCardViewPolicy,
                            fontFamily = fontFamily
                        ) {
                            openMaxLinesSettingDialog()
                        }
                    }

                    SwitchCard(
                        title = stringResource(R.string.enable_card_view_policy_title),
                        description = stringResource(R.string.enable_card_view_policy_summary),
                        modifier = cardModifier,
                        isOn = enableCardViewPolicy,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        val newValue = !enableCardViewPolicy
                        config.enableCardViewPolicy = newValue
                        mSettingsViewModel.setEnableCardViewPolicy(newValue)
                    }

                    var calendarStartDay by remember { mutableIntStateOf(config.calendarStartDay) }
                    RadioGroupCard(
                        title = stringResource(R.string.calendar_start_day_title),
                        description = stringResource(R.string.calendar_start_day_summary),
                        modifier = cardModifier,
                        options = listOf(
                            mapOf("title" to stringResource(R.string.calendar_start_day_saturday), "key" to CALENDAR_START_DAY_SATURDAY),
                            mapOf("title" to stringResource(R.string.calendar_start_day_sunday), "key" to CALENDAR_START_DAY_SUNDAY),
                            mapOf("title" to stringResource(R.string.calendar_start_day_monday), "key" to CALENDAR_START_DAY_MONDAY)
                        ),
                        selectedKey = calendarStartDay,
                        fontFamily = fontFamily
                    ) { key ->
                        calendarStartDay = key
                        config.calendarStartDay = key
                    }

                    var calendarSorting by remember { mutableIntStateOf(config.calendarSorting) }
                    RadioGroupCard(
                        title = stringResource(R.string.calendar_sort_title),
                        description = stringResource(R.string.calendar_sort_summary),
                        modifier = cardModifier,
                        options = listOf(
                            mapOf("title" to stringResource(R.string.calendar_sort_ascending), "key" to CALENDAR_SORTING_ASC),
                            mapOf("title" to stringResource(R.string.calendar_sort_descending), "key" to CALENDAR_SORTING_DESC)
                        ),
                        selectedKey = calendarSorting,
                        fontFamily = fontFamily
                    ) { key ->
                        calendarSorting = key
                        config.calendarSorting = key
                    }

                    var holdPosition by remember { mutableStateOf(config.holdPositionEnterEditScreen) }
                    SwitchCard(
                        title = stringResource(R.string.hold_position_title),
                        description = stringResource(R.string.hold_position_summary),
                        modifier = cardModifier,
                        isOn = holdPosition,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        holdPosition = !holdPosition
                        config.holdPositionEnterEditScreen = holdPosition
                    }

                    SimpleCard(
                        title = "Selective Export",
                        description = "Export your data to a text file based on selection.",
                        modifier = cardModifier,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        showExportSelectionDialog()
                    }

                    SimpleCard(
                        title = stringResource(R.string.cleanup_media_title),
                        description = stringResource(R.string.cleanup_media_summary),
                        modifier = cardModifier,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontFamily = fontFamily
                    ) {
                        handleMediaCleanup()
                    }
                }

                // --- SECTION: FONT SETTINGS ---
                CategoryTitleCard(title = stringResource(R.string.preferences_category_font))
                FlowRow(maxItemsInEachRow = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 2) {
                    LineSpacing(
                        title = "Line Spacing: $lineSpacingScaleFactor",
                        description = "Adjust the vertical distance between lines of text.",
                        modifier = cardModifier,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontSize = fontSize,
                        fontFamily = fontFamily,
                        lineSpacingScaleFactor = lineSpacingScaleFactor
                    ) { factor ->
                        config.lineSpacingScaleFactor = factor
                        mSettingsViewModel.setLineSpacingScaleFactor(factor)
                    }

                    FontSize(
                        title = "Font Size",
                        description = "Change the size of the text in your diaries.",
                        modifier = cardModifier,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontSize = fontSize,
                        fontFamily = fontFamily,
                        lineSpacingScaleFactor = lineSpacingScaleFactor,
                        callbackMinus = {
                            val newSize = (config.settingFontSize - 5).coerceAtLeast(10f)
                            config.settingFontSize = newSize
                            mSettingsViewModel.setFontSize(newSize)
                        },
                        callbackPlus = {
                            val newSize = (config.settingFontSize + 5).coerceAtMost(100f)
                            config.settingFontSize = newSize
                            mSettingsViewModel.setFontSize(newSize)
                        }
                    )

                    var boldStyleEnable by remember { mutableStateOf(config.boldStyleEnable) }
                    SwitchCard(
                        title = "Bold Text",
                        description = "Enable bold style for diary contents.",
                        modifier = cardModifier,
                        isOn = boldStyleEnable,
                        enableCardViewPolicy = enableCardViewPolicy,
                        fontSize = fontSize,
                        fontFamily = fontFamily,
                        lineSpacingScaleFactor = lineSpacingScaleFactor
                    ) {
                        boldStyleEnable = !boldStyleEnable
                        config.boldStyleEnable = boldStyleEnable
                    }
                }

                // --- SECTION: LOCK SETTINGS ---
                CategoryTitleCard(title = stringResource(R.string.preferences_category_lock))
                FlowRow(maxItemsInEachRow = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 2) {
                    var aafPinLockEnable by remember { mutableStateOf(config.aafPinLockEnable) }
                    SwitchCard(
                        title = stringResource(R.string.pin_lock_title),
                        description = stringResource(R.string.pin_lock_summary),
                        modifier = cardModifier,
                        isOn = aafPinLockEnable,
                        fontSize = fontSize,
                        fontFamily = fontFamily
                    ) {
                        handlePinLockToggle(!aafPinLockEnable) { aafPinLockEnable = it }
                    }

                    var fingerprintLockEnable by remember { mutableStateOf(config.fingerprintLockEnable) }
                    SwitchCard(
                        title = stringResource(R.string.fingerprint_lock_title),
                        description = stringResource(R.string.fingerprint_lock_summary),
                        modifier = cardModifier,
                        isOn = fingerprintLockEnable,
                        fontSize = fontSize,
                        fontFamily = fontFamily
                    ) {
                        handleFingerprintToggle(!fingerprintLockEnable) { fingerprintLockEnable = it }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    private fun handleLocationToggle(enabled: Boolean) {
        if (enabled) {
            if (hasGPSPermissions()) {
                config.enableLocationInfo = true
                mSettingsViewModel.setEnableLocationInfo(true)
            } else {
                acquireGPSPermissions(mRequestLocationSourceLauncher) {
                    config.enableLocationInfo = true
                    mSettingsViewModel.setEnableLocationInfo(true)
                    makeSnackBar("GPS provider setting is activated!!!")
                }
            }
        } else {
            config.enableLocationInfo = false
            mSettingsViewModel.setEnableLocationInfo(false)
        }
    }

    private fun handleMediaCleanup() {
        lifecycleScope.launch {
            val orphanedCount = MediaManager.getOrphanedFilesCount(this@SettingsActivity)
            if (orphanedCount > 0) {
                showAlertDialog(
                    getString(R.string.cleanup_media_confirm, orphanedCount),
                    { _, _ ->
                        lifecycleScope.launch {
                            val deleted = MediaManager.cleanupOrphanedFiles(this@SettingsActivity)
                            makeSnackBar(getString(R.string.cleanup_media_deleted_count, deleted))
                        }
                    },
                    { _, _ -> },
                    DialogMode.WARNING,
                    true
                )
            } else {
                makeSnackBar(getString(R.string.cleanup_media_no_files))
            }
        }
    }

    private fun handlePinLockToggle(enabled: Boolean, callback: (Boolean) -> Unit) {
        if (!enabled) {
            if (config.fingerprintLockEnable) {
                showAlertDialog(getString(R.string.pin_release_need_fingerprint_disable))
            } else {
                config.aafPinLockEnable = false
                callback(false)
                showAlertDialog(getString(R.string.pin_setting_release))
                applyPolicyForRecentApps()
            }
        } else {
            val intent = Intent(this, PinLockActivity::class.java)
            intent.putExtra(FingerprintLockConstants.LAUNCHING_MODE, PinLockConstants.ACTIVITY_SETTING)
            startActivity(intent)
            // Note: callback for UI update will likely happen onResume
        }
    }

    private fun handleFingerprintToggle(enabled: Boolean, callback: (Boolean) -> Unit) {
        if (!enabled) {
            config.fingerprintLockEnable = false
            callback(false)
            showAlertDialog(getString(R.string.fingerprint_setting_release))
            applyPolicyForRecentApps()
        } else {
            if (config.aafPinLockEnable) {
                val intent = Intent(this, FingerprintLockActivity::class.java)
                intent.putExtra(FingerprintLockConstants.LAUNCHING_MODE, FingerprintLockConstants.ACTIVITY_SETTING)
                startActivity(intent)
            } else {
                showAlertDialog(getString(R.string.fingerprint_lock_need_pin_setting))
            }
        }
    }

    private fun showExportSelectionDialog() {
        val options = arrayOf("Diaries", "Notes", "Tasks")
        val checkedItems = booleanArrayOf(exportSelection["diary"]!!, exportSelection["note"]!!, exportSelection["task"]!!)
        
        AlertDialog.Builder(this)
            .setTitle("Select data to export")
            .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
                val key = when (which) {
                    0 -> "diary"
                    1 -> "note"
                    else -> "task"
                }
                exportSelection[key] = isChecked
            }
            .setPositiveButton("Export") { _, _ ->
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "EasyDiary_Export_${System.currentTimeMillis()}.txt")
                }
                exportTextLauncher.launch(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openThumbnailSettingDialog() {
        var alertDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(this)
        builder.setNegativeButton(getString(android.R.string.cancel), null)
        val containerView = layoutInflater.inflate(R.layout.dialog_option_item, null, false)
        val listView = containerView.findViewById<ListView>(R.id.listView)

        var selectedIndex = 0
        val listThumbnailSize = ArrayList<Map<String, String>>()
        for (i in 40..200 step 10) {
            listThumbnailSize.add(mapOf("optionTitle" to "${i}dp x ${i}dp", "optionValue" to "$i"))
        }

        listThumbnailSize.mapIndexed { index, map ->
            val size = map["optionValue"] ?: "0"
            if (config.settingThumbnailSize == size.toFloat()) selectedIndex = index
        }

        val arrayAdapter = OptionItemAdapter(this, R.layout.item_check_label, listThumbnailSize, config.settingThumbnailSize)
        listView.adapter = arrayAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            @Suppress("UNCHECKED_CAST")
            val fontInfo = parent.adapter.getItem(position) as HashMap<String, String>
            fontInfo["optionValue"]?.let {
                config.settingThumbnailSize = it.toFloat()
            }
            alertDialog?.cancel()
        }
        alertDialog = builder.create().apply {
            updateAlertDialogWithIcon(DialogMode.SETTING, this, null, containerView, getString(R.string.thumbnail_setting_title))
        }
        listView.setSelection(selectedIndex)
    }

    private fun openDatetimeFormattingSettingDialog() {
        var alertDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(this)
        builder.setNegativeButton(getString(android.R.string.cancel), null)
        val containerView = layoutInflater.inflate(R.layout.dialog_option_item, null, false)
        val listView = containerView.findViewById<ListView>(R.id.listView)

        val listFormat = ArrayList<Map<String, String>>()
        DateTimeFormat.entries.forEach { format ->
            listFormat.add(mapOf(
                "optionTitle" to DateUtils.getDateTimeStringFromTimeMillis(System.currentTimeMillis(), format.getDateKey(), format.getTimeKey()),
                "optionValue" to format.toString()
            ))
        }

        val arrayAdapter = OptionItemAdapter(this, R.layout.item_check_label, listFormat, null, config.settingDatetimeFormat)
        listView.adapter = arrayAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            @Suppress("UNCHECKED_CAST")
            val fontInfo = parent.adapter.getItem(position) as HashMap<String, String>
            fontInfo["optionValue"]?.let {
                config.settingDatetimeFormat = it
            }
            alertDialog?.cancel()
        }
        alertDialog = builder.create().apply {
            updateAlertDialogWithIcon(DialogMode.SETTING, this, null, containerView, "Datetime formatting")
        }
        listView.setSelection(listFormat.indexOfFirst { it["optionValue"] == config.settingDatetimeFormat })
    }

    private fun openMaxLinesSettingDialog() {
        var alertDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(this)
        builder.setNegativeButton(getString(android.R.string.cancel), null)
        val containerView = layoutInflater.inflate(R.layout.dialog_option_item, null, false)
        val listView = containerView.findViewById<ListView>(R.id.listView)

        val listMaxLines = (1..20).map { mapOf("optionTitle" to getString(R.string.max_lines_value, it), "optionValue" to "$it") }
        val arrayAdapter = OptionItemAdapter(this, R.layout.item_check_label, listMaxLines, config.summaryMaxLines.toFloat())
        listView.adapter = arrayAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            @Suppress("UNCHECKED_CAST")
            val optionInfo = parent.adapter.getItem(position) as HashMap<String, String>
            optionInfo["optionValue"]?.let {
                config.summaryMaxLines = it.toInt()
            }
            alertDialog?.cancel()
        }
        alertDialog = builder.create().apply {
            updateAlertDialogWithIcon(DialogMode.SETTING, this, null, containerView, getString(R.string.max_lines_title))
        }
        listView.setSelection(config.summaryMaxLines - 1)
    }

    override fun onResume() {
        super.onResume()
        if (config.isThemeChanged) {
            config.isThemeChanged = false
            startMainActivityWithClearTask()
        }
    }
}
