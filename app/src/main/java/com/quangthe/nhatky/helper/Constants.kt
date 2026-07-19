package com.quangthe.nhatky.helper

import android.Manifest

/**
 * Created by CHO HANJOONG on 2018-02-09.
 */

/***************************************************************************************************
 *   WorkManager
 *
 ***************************************************************************************************/


/***************************************************************************************************
 *   Save Instance State
 *
 ***************************************************************************************************/
const val LIST_URI_STRING = "list_uri_string"
const val LIST_MIME_TYPE_STRING = "list_mime_type_string"
const val SELECTED_YEAR = "selected_year"
const val SELECTED_MONTH = "selected_month"
const val SELECTED_DAY = "selected_day"
const val SELECTED_HOUR = "selected_hour"
const val SELECTED_MINUTE = "selected_minute"
const val SELECTED_SECOND = "selected_second"
const val FILTER_VIEW_VISIBLE = "filter_view_visible"
const val FILTER_START_ENABLE = "filter_start_enable"
const val FILTER_START_YEAR = "filter_start_year"
const val FILTER_START_MONTH = "filter_start_month"
const val FILTER_START_DATE = "filter_start_date"
const val FILTER_END_ENABLE = "filter_end_enable"
const val FILTER_END_YEAR = "filter_end_year"
const val FILTER_END_MONTH = "filter_end_month"
const val FILTER_END_DATE = "filter_end_date"
const val FILTER_QUERY = "filter_query"

/***************************************************************************************************
 *   Calendar constants
 *
 ***************************************************************************************************/
const val SETTING_CALENDAR_START_DAY = "setting_calendar_start_day"
const val SETTING_CALENDAR_SORTING = "setting_calendar_sorting"
const val SETTING_CALENDAR_FONT_SCALE = "setting_calendar_font_scale"
const val DAILY_SCALE = -1
const val DAILY_STOCK = -1
const val DEFAULT_CALENDAR_FONT_SCALE = 1.0f
const val SETTING_FONT_NAME = "setting_font_name"
const val CALENDAR_SORTING_ASC = 1
const val CALENDAR_SORTING_DESC = 2
const val CALENDAR_START_DAY_SUNDAY = 1
const val CALENDAR_START_DAY_MONDAY = 2

// const val CALENDAR_START_DAY_TUESDAY   = 3
// const val CALENDAR_START_DAY_WEDNESDAY = 4
// const val CALENDAR_START_DAY_THURSDAY  = 5
// const val CALENDAR_START_DAY_FRIDAY    = 6
const val CALENDAR_START_DAY_SATURDAY = 7

/***************************************************************************************************
 *   Showcase constants
 *
 ***************************************************************************************************/
const val SHOWCASE_SINGLE_SHOT_READ_DIARY_NUMBER = 0
const val SHOWCASE_SINGLE_SHOT_CREATE_DIARY_NUMBER = 1
const val SHOWCASE_SINGLE_SHOT_READ_DIARY_DETAIL_NUMBER = 2
const val SHOWCASE_SINGLE_SHOT_POST_CARD_NUMBER = 3

/***************************************************************************************************
 *   Notification constants
 *
 ***************************************************************************************************/
const val NOTIFICATION_CHANNEL_ID = "easy_diary_channel_id"
const val NOTIFICATION_CHANNEL_DESCRIPTION =
    "This channel is used for 'Easy-Diary' data backup and recovery operations."
const val NOTIFICATION_COMPRESS_ID = 1004
const val NOTIFICATION_DECOMPRESS_ID = 1005
const val NOTIFICATION_ID = "notification_id"
const val NOTIFICATION_INFO = "notification_info"

/***************************************************************************************************
 *   Path constants
 *
 ***************************************************************************************************/
const val WORKING_DIRECTORY = "/AAFactory/EasyDiary/"
const val DIARY_PHOTO_DIRECTORY = "${WORKING_DIRECTORY}Photos/"
const val DIARY_POSTCARD_DIRECTORY = "${WORKING_DIRECTORY}Postcards/"
const val MARKDOWN_DIRECTORY = "${WORKING_DIRECTORY}Markdown/"
const val BACKUP_DB_DIRECTORY = "${WORKING_DIRECTORY}Backup/Database/"
const val DIARY_AUDIO_DIRECTORY = "${WORKING_DIRECTORY}Audio/"
const val DIARY_VIDEO_DIRECTORY = "${WORKING_DIRECTORY}Video/"
const val USER_CUSTOM_FONTS_DIRECTORY = "${WORKING_DIRECTORY}Fonts/"

/***************************************************************************************************
 *   Request code constants
 *
 ***************************************************************************************************/
// startActivityForResult Request Code: Permission
const val REQUEST_CODE_EXTERNAL_STORAGE = 1
const val REQUEST_CODE_EXTERNAL_STORAGE_WITH_SHARE_DIARY_CARD = 2

// const val REQUEST_CODE_EXTERNAL_STORAGE_WITH_FONT_SETTING = 3
const val REQUEST_CODE_EXTERNAL_STORAGE_WITH_MARKDOWN = 4

const val REQUEST_CODE_EXTERNAL_STORAGE_WITH_EXPORT_DATABASE = 6
const val REQUEST_CODE_EXTERNAL_STORAGE_WITH_IMPORT_DATABASE = 7
const val REQUEST_CODE_EXTERNAL_STORAGE_WITH_DELETE_DATABASE = 8

// const val REQUEST_CODE_CAPTURE_CAMERA = 9
const val REQUEST_CODE_EXTERNAL_STORAGE_WITH_EXPORT_FULL_BACKUP = 10

// const val REQUEST_CODE_ACTION_LOCATION_SOURCE_SETTINGS = 11
const val REQUEST_CODE_NOTIFICATION = 12


// startActivityForResult Request Code: Etc
// const val REQUEST_CODE_LOCK_SETTING = 21

// startActivityForResult Request Code: ColorPicker
// const val REQUEST_CODE_BACKGROUND_COLOR_PICKER = 31
// const val REQUEST_CODE_TEXT_COLOR_PICKER = 32

const val REQUEST_CODE_SAF_WRITE_ZIP = 40

const val REQUEST_CODE_SAF_WRITE_DATABASE = 42
const val REQUEST_CODE_SAF_READ_ZIP = 43
const val REQUEST_CODE_SAF_READ_DATABASE = 44
// const val REQUEST_CODE_SAF_HTML_BOOK = 45

// const val REQUEST_CODE_FONT_PICK = 103
// const val REQUEST_CODE_UPDATE_DAILY_SYMBOL_FILTER = 104

/***************************************************************************************************
 *   Diary execution mode (Intent)
 *
 ***************************************************************************************************/
// IntroActivity Delay Message ID
const val START_MAIN_ACTIVITY = 0
const val START_DASHBOARD_ACTIVITY = 1

// Diary Mode
const val DIARY_EXECUTION_MODE = "diary_execution_mode"

// Diary Mode Case01: DiaryMainWidget or getOpenAlarmTabIntent
const val EXECUTION_MODE_ACCESS_FROM_OUTSIDE = "execution_mode_access_from_outside"

// (Removed: EXECUTION_MODE_WELCOME_DASHBOARD)

/***************************************************************************************************
 *   Diary view mode for DiaryMainActivity grid item layout (Bundle)
 *   com.quangthe.nhatky.enums.DiaryMode
 ***************************************************************************************************/
const val DIARY_MODE = "diary_mode"

/***************************************************************************************************
 *   SAF mime type
 *
 ***************************************************************************************************/
const val MIME_TYPE_BINARY = "application/octet-stream"
const val MIME_TYPE_ZIP = "application/zip"
const val MIME_TYPE_XLS = "application/vnd.ms-excel"
const val MIME_TYPE_DATABASE = "application/octet-stream"
const val MIME_TYPE_JPEG = "image/jpeg"
const val MIME_TYPE_HTML = "text/html"

/***************************************************************************************************
 *   Theme Color
 *
 ***************************************************************************************************/
const val AUTO_SETUP_SCREEN_BACKGROUND_DARKEN_COLOR = -30
const val EASYDIARY_THEME_PRIMARY_COLOR = "#07ABB3"
const val EASYDIARY_THEME_BACKGROUND_COLOR = "#FFFFFF"
const val EASYDIARY_THEME_SCREEN_BACKGROUND_COLOR = "#EAEAEA"
const val EASYDIARY_THEME_TEXT_COLOR = "#4D4C4C"
const val DARK_THEME_PRIMARY_COLOR = "#000000"
const val DARK_THEME_BACKGROUND_COLOR = "#464646"
const val DARK_THEME_SCREEN_BACKGROUND_COLOR = "#292929"
const val DARK_THEME_TEXT_COLOR = "#BBBBBB"
const val GREEN_THEME_PRIMARY_COLOR = "#43A047"
const val GREEN_THEME_BACKGROUND_COLOR = "#FFF5E0"
const val GREEN_THEME_SCREEN_BACKGROUND_COLOR = "#EFFFEF"
const val GREEN_THEME_TEXT_COLOR = "#4D4C4C"

/***************************************************************************************************
 *   Debug Options
 *
 ***************************************************************************************************/
const val SETTING_DEBUG_OPTION_TOAST_LOCATION = "setting_debug_option_toast_location"
const val SETTING_DEBUG_OPTION_TOAST_ATTACHED_PHOTO = "setting_debug_option_toast_attached_photo"
const val SETTING_DEBUG_OPTION_TOAST_NOTIFICATION_INFO =
    "setting_debug_option_toast_notification_info"


const val SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_DIARY_SEQUENCE =
    "setting_debug_option_visible_diary_sequence"
const val SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_ALARM_SEQUENCE =
    "setting_debug_option_visible_alarm_sequence"
const val SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_CHART_WEIGHT =
    "setting_debug_option_visible_chart_weight"
const val SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_CHART_STOCK =
    "setting_debug_option_visible_chart_stock"
const val SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_FONT_PREVIEW_EMOJI =
    "setting_debug_option_visible_font_preview_emoji"
const val SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_TEMPORARY_DIARY =
    "setting_debug_option_visible_temporary_diary"
const val SETTING_DEBUG_OPTION_DISPLAY_VISIBLE_TREE_STATUS =
    "setting_debug_option_visible_tree_status"

/***************************************************************************************************
 *   Etc constants
 *
 ***************************************************************************************************/
val EXTERNAL_STORAGE_PERMISSIONS =
    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

const val INIT_DUMMY_DATA_FLAG = "init_dummy_data"

const val APP_LOCK_ENABLE = "application_lock"

// const val APP_LOCK_REQUEST_PASSWORD = "lock_password"
const val APP_LOCK_SAVED_PASSWORD = "application_lock_password"
const val APP_LOCK_DEFAULT_PASSWORD = "0000"

const val DIARY_SEQUENCE = "diary_sequence"
const val SELECTED_SEARCH_QUERY = "selected_search_query"
const val DIARY_SEARCH_QUERY_CASE_SENSITIVE = "diary_search_query_case_sensitive"
const val DIARY_CONTENTS_SCROLL_Y = "diary_contents_scroll_y"
const val DIARY_ATTACH_PHOTO_INDEX = "diary_attach_photo_index"
const val DIARY_ENCRYPT_PASSWORD = "diary_encrypt_password"
const val POSTCARD_SEQUENCE = "postcard_sequence"

// const val OPEN_URL_INFO = "open_url_info"
const val THUMBNAIL_BACKGROUND_ALPHA = 170
const val THUMBNAIL_BACKGROUND_ALPHA_HIGH = 220
const val THUMBNAIL_BACKGROUND_ALPHA_LOW = 70

const val SUPPORT_LANGUAGE_FONT_SIZE_DEFAULT_SP = 20
const val UN_SUPPORT_LANGUAGE_FONT_SIZE_DEFAULT_SP = 15
const val CHART_LABEL_FONT_SIZE_DEFAULT_DP = 11F
const val THUMBNAIL_SIZE_DEFAULT_DP = 50


const val POSTCARD_BG_COLOR = "postcard_bg_color"
const val POSTCARD_TEXT_COLOR = "postcard_text_color"
const val POSTCARD_BG_COLOR_VALUE = -0x1
const val POSTCARD_TEXT_COLOR_VALUE = -0xb5b5b4
const val POSTCARD_CROP_MODE = "postcard_crop_mode"

// const val APP_BACKGROUND_ALPHA = 200
// const val INTRO_BACKGROUND_ALPHA = 255
const val SETTING_FONT_SIZE = "font_size"


const val SETTING_THUMBNAIL_SIZE = "thumbnail_size"
const val SETTING_BOLD_STYLE = "setting_bold_style"
const val SETTING_MULTIPLE_PICKER = "setting_multiple_picker"
const val SETTING_FINGERPRINT_LOCK = "setting_fingerprint_lock"
const val SETTING_CONTENTS_SUMMARY = "setting_contents_summary"
const val SETTING_CLEAR_LEGACY_TOKEN = "setting_clear_legacy_token"
const val SETTING_SUMMARY_MAX_LINES = "setting_summary_max_lines"
const val SETTING_SUMMARY_MAX_LINES_DEFAULT = 3
const val SETTING_COUNT_CHARACTERS = "setting_count_characters"
const val SETTING_PHOTO_HIGHLIGHT = "setting_photo_highlight"
const val SETTING_ENABLE_DEBUG_CONSOLE = "setting_enable_debug_console"

const val SETTING_LOCATION_INFO = "setting_location_info"

const val SETTING_POSTCARD_SPAN_COUNT_LANDSCAPE = "setting_postcard_span_count_landscape"
const val SETTING_POSTCARD_SPAN_COUNT_PORTRAIT = "setting_postcard_span_count_portrait"
const val SETTING_DIARY_MAIN_SPAN_COUNT_LANDSCAPE = "setting_diary_main_span_count_landscape"
const val SETTING_DIARY_MAIN_SPAN_COUNT_PORTRAIT = "setting_diary_main_span_count_portrait"
const val SETTING_GALLERY_SPAN_COUNT_LANDSCAPE = "setting_gallery_span_count_landscape"
const val SETTING_GALLERY_SPAN_COUNT_PORTRAIT = "setting_gallery_span_count_portrait"
const val SETTING_ENABLE_STATUSBAR_DARKEN_COLOR = "setting_enable_statusbar_darken_color"

const val SETTING_ENABLE_DDAY_FLEXBOX_LAYOUT = "setting_enable_dday_flexbox_layout"
const val SETTING_ENABLE_DASHBOARD_CALENDAR = "setting_enable_dashboard_calendar"
const val SETTING_DATETIME_FORMAT = "setting_datetime_format"
const val SETTING_ENABLE_MARKDOWN = "setting_enable_markdown"
const val SETTING_VISIBLE_UNLINKED_PHOTOS = "setting_visible_unlinked_photos"
const val SETTING_DISABLE_FUTURE_DIARY = "setting_disable_future_diary"

const val HOLD_POSITION_ENTER_EDIT_SCREEN = "hold_position_enter_edit_screen"
const val FINGERPRINT_ENCRYPT_DATA = "fingerprint_encrypt_data"
const val FINGERPRINT_ENCRYPT_DATA_IV = "fingerprint_encrypt_data_iv"
const val FINGERPRINT_AUTHENTICATION_FAIL_COUNT = "fingerprint_authentication_fail_count"
const val ENABLE_CARD_VIEW_POLICY = "enable_card_view_policy"


const val CONTENT_URI_PREFIX = "content:/"
const val FILE_URI_PREFIX = "file:/"
const val LINE_SPACING_SCALE_FACTOR = "line_spacing_scale_factor"
const val LINE_SPACING_SCALE_DEFAULT = 1.0F
const val DIARY_LAST_BACKUP_TIMESTAMP_GOOGLE_DRIVE = "diary_last_backup_time_google_drive"
const val DIARY_LAST_BACKUP_TIMESTAMP_LOCAL = "diary_last_backup_time_local"
const val PHOTO_LAST_BACKUP_TIMESTAMP_GOOGLE_DRIVE = "photo_last_backup_time_google_drive"
const val CAPTURE_CAMERA_FILE_NAME = "capture.jpg"
const val UPDATE_SHARED_PREFERENCE = "update_shared_preference"
const val APP_EXECUTION_COUNT = "count_app_execution"
const val APP_EXECUTION_COUNT_DEFAULT = 0

const val SETTING_FLAG_EXPORT_GOOGLE_DRIVE = 1
const val SETTING_FLAG_IMPORT_GOOGLE_DRIVE = 2
const val SETTING_FLAG_EXPORT_PHOTO_GOOGLE_DRIVE = 3
const val SETTING_FLAG_IMPORT_PHOTO_GOOGLE_DRIVE = 4

const val PREVIOUS_ACTIVITY = "previous_activity"
const val PREVIOUS_ACTIVITY_CREATE = 1
const val AAF_TEST = "aaf-test"
const val DOZE_SCHEDULE = "doze_schedule"
const val PHOTO_CORNER_RADIUS_SCALE_FACTOR_NORMAL = 0.05F
const val PHOTO_CORNER_RADIUS_SCALE_FACTOR_SMALL = 0.02F

const val ATTACH_PHOTO_CONTAINER_CARD_PADDING_DP =
    30F // workaround for cardview inner padding issue
const val ATTACH_PHOTO_MARGIN_DP = 3F
const val ATTACH_PHOTO_CARD_CONTENT_PADDING_DP = 3F

/***************************************************************************************************
 *   AAFactory Legacy
 *
 ***************************************************************************************************/
const val PERMISSION_ACCESS_COARSE_LOCATION = 10
const val PERMISSION_ACCESS_FINE_LOCATION = 11

const val AAF_PIN_LOCK_PAUSE_MILLIS = "aaf_pin_lock_pause_millis"
const val AAF_THEME_CHANGE = "aaf_theme_change"

/***************************************************************************************************
 *   Dev
 *
 ***************************************************************************************************/
const val DEV_STOCK_CHART_OPTIONS_FROM_MILLIS = "devStockChartOptionsFromMillis"
const val DEV_STOCK_ENABLE_EVALUATE_PRICE = "devStockEnableEvaluatePrice"
const val DEV_STOCK_ENABLE_PRINCIPAL_HIGHLIGHT = "devStockEnablePrincipalHighlight"
const val DEV_TAG_LOCATION_MANAGER = "dev_tag_location_manager"
const val DEV_SYNC_MARKDOWN_ALL = "dev_sync_markdown_all"
const val DEV_SYNC_MARKDOWN_DEV = "dev_sync_markdown_dev"
const val DEV_SYNC_MARKDOWN_ETC = "dev_sync_markdown_etc"
const val DEV_SYNC_MARKDOWN_LIFE = "dev_sync_markdown_life"
const val DEV_SYNC_MARKDOWN_STOCK_FICS = "dev_sync_markdown_stock_fics"
const val DEV_SYNC_MARKDOWN_STOCK_ETF = "dev_sync_markdown_stock_etf"
const val DEV_SYNC_MARKDOWN_STOCK_KNOWLEDGE = "dev_sync_markdown_stock_knowledge"

object DiaryComponentConstants {
    const val MODE_FLAG = "mode_flag"
    const val MODE_PREVIOUS_100 = "mode_previous_100"
    const val MODE_TASK_TODO = "mode_task_todo"
    const val MODE_TASK_DOING = "mode_task_doing"
    const val MODE_TASK_DONE = "mode_task_done"
    const val MODE_TASK_CANCEL = "mode_task_cancel"
    const val MODE_FUTURE = "mode_future"
}

object PhotoHighlightConstants {
    const val PAGE_STYLE = "page_style"
    const val REVEAL_WIDTH = "reveal_width"
    const val PAGE_MARGIN = "page_margin"
    const val AUTO_PLAY = "auto_play"
}

object ColorConstants {
    const val DARK_GREY = -13421773
    const val HIGHLIGHT_COLOR: Int = 0x9FFFFF00.toInt()
}

object ComposeConstants {
    const val VERTICAL_PADDING = 5F
    const val HORIZONTAL_PADDING = 5F
    const val ROUNDED_CORNER_SHAPE_SIZE = 8F
}

object SettingConstants {
    const val GENERIC_PERM_HANDLER = 100
    const val INITIALIZE_TIME_MILLIS = "initialize_time_millis"
}

object NotificationConstants {
    // BaseDevActivity
    const val ACTION_DEV_DISMISS = "com.quangthe.nhatky.services.ACTION_DEV_DISMISS"
    const val ACTION_DEV_TOAST = "com.quangthe.nhatky.services.ACTION_DEV_TOAST"

    const val ACTION_DISMISS_COMPRESS = "com.quangthe.nhatky.services.action.ACTION_DISMISS_COMPRESS"
    const val ACTION_DISMISS_DECOMPRESS = "com.quangthe.nhatky.services.action.ACTION_DISMISS_DECOMPRESS"
}

object GDriveConstants {
    const val MIME_TYPE_GOOGLE_APPS_FOLDER = "application/vnd.google-apps.folder"
    const val MIME_TYPE_AAF_EASY_DIARY_PHOTO = "aaf/easy.diary.photo"

    const val AAF_ROOT_FOLDER_NAME = "AAFactoty"
    const val AAF_EASY_DIARY_PHOTO_FOLDER_NAME = "aaf-easydiary_photos"
    const val AAF_EASY_DIARY_DATABASE_FOLDER_NAME = "aaf-easydiary_db"

    const val WORKING_FOLDER_ID = "working-folder-id"
}

object TransitionConstants {
    const val DEFAULT = 0
    const val BOTTOM_TO_TOP = 1
    const val TOP_TO_BOTTOM = 2
}

object WidgetConstants {
    const val OPEN_WRITE_PAGE = "open_write_page"
    const val OPEN_READ_PAGE = "open_read_page"
    const val UPDATE_WIDGET = "update_widget"
}

object WorkerConstants {
    const val URI_STRING = "uri_string"
    const val WORK_MODE_BACKUP = "work_mode_backup"
    const val WORK_MODE_RECOVERY = "work_mode_recovery"
}

object DateUtilConstants {
    const val DATE_PATTERN_DASH = "yyyy-MM-dd"
    const val DATE_TIME_PATTERN_WITHOUT_DASH = "yyyyMMddHHmmss"
}

object ChartConstants {
    const val CHART_TITLE = "chartTitle"
}

object StatisticsConstants {
    const val CHART_MODE = "chart_mode"
    const val MODE_SINGLE_LINE_CHART_WEIGHT = "mode_single_line_chart_weight"
    const val MODE_SINGLE_LINE_CHART_STOCK = "mode_single_line_chart_stock"
    const val MODE_SINGLE_BAR_CHART_WRITING = "mode_single_bar_chart_writing"
}

object DiaryEditingConstants {
    const val FOCUS_TITLE = 0
    const val FOCUS_CONTENTS = 1
    const val DIARY_SEQUENCE_TEMPORARY = -1
    const val DIARY_SEQUENCE_INIT = 0
    const val DIARY_ORIGIN_SEQUENCE_INIT = 0
}

object PhotoFlexItemOptionConstants {
    const val ITEM_INDEX = "item_index"
    const val VIEW_MODE = "view_index"
    const val FILTER_MODE = "filter_index"
    const val PHOTO_URI = "photo_uri"
    const val FORCE_SINGLE_PHOTO_POSITION = "force_single_photo_position"
}

object FingerprintLockConstants {
    const val TAG = "FingerprintLockActivity"
    const val KEY_NAME = "me.blog.korn123"
    const val DUMMY_ENCRYPT_DATA = "aaf-easydiary"
    const val LAUNCHING_MODE = "launching_mode"
    const val ACTIVITY_SETTING = "activity_setting"
    const val ACTIVITY_UNLOCK = "activity_unlock"
}

object DiaryReadingConstants {
    const val ENCRYPTION = "encryption"
    const val DECRYPTION = "decryption"
    const val EDITING = "editing"
}

object DashboardConstants {
    const val MODE_FLAG = "mode"
    const val MODE_LIFETIME = "lifetime"
    const val MODE_LAST_MONTH = "lastMonth"
    const val MODE_LAST_WEEK = "lastWeek"
}

object PrepareReleaseConstants {
    const val SYNC_RELEASE_STRING = "sync_release_string"
    const val SYNC_NEW_STRING = "sync_new_string"
    const val SYNC_RELEASE_NOTE = "sync_release_note"
}

object MarkdownConstants {
    const val OPEN_URL_INFO = "open_url_info"
    const val OPEN_URL_DESCRIPTION = "open_url_description"
    const val FORCE_APPEND_CODE_BLOCK = "force_append_code_block"
}

object PinLockConstants {
    const val LAUNCHING_MODE = "launching_mode"
    const val ACTIVITY_SETTING = "activity_setting"
    const val ACTIVITY_UNLOCK = "activity_unlock"
}

object PostcardConstants {
    const val GUIDE_MESSAGE = "No information"
    const val POSTCARD_DATE_FORMAT = "yyyyMMddHHmmss"
}

object GalleryConstants {
    const val GUIDE_MESSAGE = "No information"
}


object TreeConstants {
    const val IS_TREE_TIMELINE_LAUNCH_MODE_DEFAULT = "is_tree_timeline_launch_mode_default"
    const val ROOT_NODE = "root_node"
    const val SORT_OPTION_ASC = "asc"
    const val SORT_OPTION_DESC = "desc"
    const val LEVEL_ZERO = 0
    const val LEVEL_START = 1
}

object ScrollDirection {
    const val UP = -1
    const val DOWN = 1
}
