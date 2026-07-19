package com.quangthe.nhatky.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import com.quangthe.nhatky.helper.DEV_SYNC_MARKDOWN_ALL
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.commons.models.Release
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.quangthe.nhatky.commons.utils.BitmapUtils
import com.quangthe.nhatky.commons.utils.DateUtils
import com.quangthe.nhatky.commons.utils.EasyDiaryUtils
import com.quangthe.nhatky.BuildConfig
import com.quangthe.nhatky.R
import com.quangthe.nhatky.activities.BaseSimpleActivity
import com.quangthe.nhatky.compose.DiaryMainActivity
import com.quangthe.nhatky.activities.EasyDiaryActivity
import com.quangthe.nhatky.compose.FingerprintLockActivity
import com.quangthe.nhatky.compose.PinLockActivity
import com.quangthe.nhatky.adapters.OptionItemAdapter
import com.quangthe.nhatky.dialogs.WhatsNewDialog
import com.quangthe.nhatky.enums.GridSpanMode
import com.quangthe.nhatky.helper.AAF_TEST
import com.quangthe.nhatky.helper.BACKUP_DB_DIRECTORY
import com.quangthe.nhatky.helper.DIARY_EXECUTION_MODE
import com.quangthe.nhatky.helper.DIARY_PHOTO_DIRECTORY
import com.quangthe.nhatky.helper.DIARY_POSTCARD_DIRECTORY
import com.quangthe.nhatky.helper.EXECUTION_MODE_ACCESS_FROM_OUTSIDE
import com.quangthe.nhatky.helper.EXTERNAL_STORAGE_PERMISSIONS
import com.quangthe.nhatky.helper.FILE_URI_PREFIX
import com.quangthe.nhatky.helper.FingerprintLockConstants
import com.quangthe.nhatky.helper.PERMISSION_ACCESS_COARSE_LOCATION
import com.quangthe.nhatky.helper.PERMISSION_ACCESS_FINE_LOCATION
import com.quangthe.nhatky.helper.PinLockConstants

import com.quangthe.nhatky.helper.TransitionHelper
import com.quangthe.nhatky.helper.USER_CUSTOM_FONTS_DIRECTORY
import com.quangthe.nhatky.helper.WORKING_DIRECTORY
import com.quangthe.nhatky.models.Diary
import com.quangthe.nhatky.models.PhotoUri
import com.quangthe.nhatky.views.SlidingTabLayout
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/***************************************************************************************************
 *   Confirm Permissions
 *
 ***************************************************************************************************/
fun Activity.confirmPermission(
    permissions: Array<String>,
    requestCode: Int,
) {
    if (permissions.any { permission -> ActivityCompat.shouldShowRequestPermissionRationale(this, permission) }) {
        AlertDialog
            .Builder(this)
            .setMessage(getString(R.string.permission_confirmation_dialog_message))
            .setTitle(getString(R.string.permission_confirmation_dialog_title))
            .setPositiveButton(getString(R.string.ok)) { _, _ -> ActivityCompat.requestPermissions(this, permissions, requestCode) }
            .show()
    } else {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }
}

fun Activity.confirmExternalStoragePermission(
    permissions: Array<String>,
    activityResultLauncher: ActivityResultLauncher<Array<String>>,
) {
    if (permissions.any { permission -> ActivityCompat.shouldShowRequestPermissionRationale(this, permission) }) {
        AlertDialog
            .Builder(this)
            .setMessage(getString(R.string.permission_confirmation_dialog_message))
            .setTitle(getString(R.string.permission_confirmation_dialog_title))
            .setPositiveButton(getString(R.string.ok)) { _, _ -> activityResultLauncher.launch(permissions) }
            .show()
    } else {
        activityResultLauncher.launch(permissions)
    }
}

/***************************************************************************************************
 *   Messages
 *
 ***************************************************************************************************/
fun Activity.makeSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
) {
    Snackbar
        .make(findViewById(android.R.id.content), message, duration)
        .setBackgroundTint(config.primaryColor)
        .setTextColor(Color.WHITE)
        .setAction("Action", null)
        .show()
}

fun Activity.showBetaFeatureMessage() {
    makeSnackBar("\uD83D\uDEA7 This feature is currently in beta testing.", Snackbar.LENGTH_LONG)
}

/***************************************************************************************************
 *   Screen Dimension
 *
 ***************************************************************************************************/

fun Activity.isLandScape(): Boolean = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Activity.actionBarHeight(): Int {
    val typedValue = TypedValue()
    var actionBarHeight = 0
    if (theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
        actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    }
    return actionBarHeight
}

fun Activity.statusBarHeight(): Int {
    var statusBarHeight = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
}

fun Activity.topBarHeight(): Int = actionBarHeight().plus(statusBarHeight())

fun Activity.navigationBarHeight(): Int {
    var navigationBarHeight = 0
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        navigationBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return navigationBarHeight
}

fun Activity.getDefaultDisplay(): Point {
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

fun Activity.getDashboardCardWidth(ratio: Float): Int {
    val scaleFactor = if (isLandScape()) 0.5F else 1F
    return getDefaultDisplay()
        .x
        .times(ratio)
        .times(scaleFactor)
        .toInt()
}

fun Activity.getDisplayMetrics(): DisplayMetrics {
    val outMetrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val display = display
        display?.getMetrics(outMetrics)
    } else {
        val display = windowManager.defaultDisplay
        display.getMetrics(outMetrics)
    }
    return outMetrics
}

/**
 * í™”ë©´ ì¢Œìš° ì‹œìŠ¤í…œì˜ì—­ ì¸ì…‹ ì²˜ë¦¬ë¥¼ í•¨
 *
 * ë¨¸í‹°ë¦¬ì–¼ ì•±ë°”ì˜ ê²½ìš° XMLë ˆì´ì•„ì›ƒ ì‚¬ìš© ì‹œ ê°€ë¡œí™”ë©´ì—ì„œ ì•±ë°” UIê°€ ì‹œìŠ¤í…œì˜ì—­ì— ê°€ë ¤ì§ˆ ìˆ˜ ìžˆìœ¼ë‚˜ XMLë ˆë²¨ì—ì„œ ì²˜ë¦¬í•  ë°©ë²•ì´ ì—†ì–´ì„œ
 * ì½”ë“œì—ì„œ ì§ì ‘ ì²˜ë¦¬í•´ì•¼ í•¨
 */
fun Activity.applyHorizontalInsets() {
    if (isLandScape()) {
        val rootHolder = findViewById<View>(R.id.main_holder)
        if (rootHolder != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootHolder) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val layoutParams = v.layoutParams
                if (layoutParams is ViewGroup.MarginLayoutParams) {
                    layoutParams.rightMargin = systemBars.right
                    layoutParams.leftMargin = systemBars.left
                    v.layoutParams = layoutParams
                }
                insets
            }
            ViewCompat.requestApplyInsets(rootHolder)
        }


    }
}

/**
 * - ì• í”Œë¦¬ì¼€ì´ì…˜ í™”ë©´ì´ í•˜ë‹¨ ë‚´ë¹„ê²Œì´ì…˜ ì˜ì—­ê¹Œì§€ í™•ìž¥ë˜ì–´ ì‚¬ìš©ë˜ëŠ” ê²½ìš° íŒŒë¼ë¯¸í„°ë¡œ ë„˜ê²¨ë°›ì€
 * ë·°ì˜ í•˜ë‹¨ì— ë‚´ë¹„ê²Œì´ì…˜ì˜ì—­ ë†’ì´ê°’ë§Œí¼ ë§ˆì§„ì„ ì¶”ê°€í•¨
 * - Version SDK 35 ë¯¸ë§Œì¸ ê²½ìš° Edege to Edge ëª¨ë“œê°€ ê°•ì œ ì ìš©ë˜ì§€ ì•Šê³  ì•±ì´ ì‹œìŠ¤í…œ UI ë’¤ë¡œ í™•ìž¥ë˜ì§€ ì•Šê¸° ë•Œë¬¸ì—
 * ë²„ì „ ì²´í¬ë¥¼ í•˜ì§€ ì•ŠìŒ
 */
fun Activity.applyBottomNavigationInsets(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val layoutParams = v.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            layoutParams.bottomMargin = systemBars.bottom
            v.layoutParams = layoutParams
        }
        insets
    }
}

/**
 * Version SDK 35 ì´ìƒì˜ ê²½ìš° IME ë° Navigation Bar ì˜ì—­ì˜ ì¸ì…‹ì„ ì ìš©í•¨
 * ì´ ê¸°ëŠ¥ì€ ì„¸ë¡œí™”ë©´ ëª¨ë“œì—ì„œë§Œ ì ìš©ë˜ë©°, ê°€ë¡œí™”ë©´ ëª¨ë“œì—ì„œëŠ” ì ìš©ë˜ì§€ ì•ŠìŒ
 */
fun Activity.applyBottomInsets(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        val layoutParams = v.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            layoutParams.bottomMargin = Math.max(systemBars.bottom, ime.bottom)
            v.layoutParams = layoutParams
        }
        insets
    }
}

/**
 * Version SDK 35 ì´ìƒì˜ ê°€ë¡œí™”ë©´ ëª¨ë“œì¸ ê²½ìš° ê°•ì œë¡œ ì‹œìŠ¤í…œë°”ë¥¼ ìˆ¨ê¹€ì²˜ë¦¬ í•¨
 */
fun Activity.hideSystemBarsVanillaIceCreamPlusIsLandScape() {
    if (isVanillaIceCreamPlus() && isLandScape()) {
        // From version 15, the system bar area is forcibly extended
        // In landscape mode, the position of the navigation bar varies depending on system settings
        // Buttons: Right side of the screen
        // Gesture navigation: Bottom of the screen
        // When the navigation bar is transparently (forcibly) placed on the right side,
        // there is no way to properly handle the Material ActionBar area
        hideSystemBars()
    }
}

fun Activity.hideSystemBars() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        window.insetsController?.hide(WindowInsets.Type.systemBars())
        window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * ðŸ’¡ ì‹œìŠ¤í…œ ìƒíƒœë°”ì˜ ë°°ê²½ì»¬ëŸ¬ ë˜ëŠ” í…ìŠ¤íŠ¸(ì•„ì´ì½˜) ì»¬ëŸ¬ë¥¼ ë³€ê²½í•¨
 *  - Version SDK 35 ì´ìƒ: í…ìŠ¤íŠ¸(ì•„ì´ì½˜) ì»¬ëŸ¬ë¥¼ ë³€ê²½í•¨
 *  - Version SDK 35 ë¯¸ë§Œ: ë°°ê²½ìƒ‰ì„ ë°˜íˆ¬ëª… ì²˜ë¦¬í•¨
 *  - ìƒë‹¨ ì•¡ì…˜ë°” ì—†ì´ ì „ì²´í™”ë©´ìœ¼ë¡œ í™”ë©´ì´ í™•ìž¥ë˜ì–´ ì‚¬ìš©ë˜ëŠ” ê²½ìš°ì— í˜¸ì¶œí•˜ëŠ” ê²ƒì´ ê¸°ë³¸ìž„
 */
fun Activity.applyFullScreenStatusBarTheme(checkColor: Int = config.screenBackgroundColor) {
    if (isVanillaIceCreamPlus()) {
        // true: ë°ì€ ë°°ê²½ â†’ ê²€ì • í…ìŠ¤íŠ¸ (light status bar icons)
        // false: ì–´ë‘ìš´ ë°°ê²½ â†’ í°ìƒ‰ í…ìŠ¤íŠ¸
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = isColorLight(checkColor)
    } else {
        @Suppress("DEPRECATION")
        window.statusBarColor = ColorUtils.setAlphaComponent(config.primaryColor, 150)

        val color =
            if (isColorLight(config.primaryColor)) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Black
        @Suppress("DEPRECATION")
        window.navigationBarColor = ColorUtils.setAlphaComponent(color.toArgb(), 150)
    }
}

fun Activity.updateStatusBarAppearance(checkColor: Int = config.primaryColor) {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isColorLight(checkColor)
}

fun Activity.updateNavigationBarAppearance(checkColor: Int = config.primaryColor) {
    if (isVanillaIceCreamPlus()) {
        window.navigationBarColor = Color.TRANSPARENT
    } else {
        val color =
            if (isColorLight(checkColor)) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.Black
        window.navigationBarColor = color.toArgb()
    }

    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightNavigationBars = isColorLight(checkColor)
    }
}

fun Activity.getSystemBarColor(): Int = if (isColorLight(config.primaryColor)) Color.WHITE else Color.BLACK

/***************************************************************************************************
 *   etc functions
 *
 ***************************************************************************************************/
fun Activity.resumeLock() {
    if (config.aafPinLockPauseMillis > 0L && System.currentTimeMillis() - config.aafPinLockPauseMillis > 1000) {
        when {
            config.fingerprintLockEnable -> {
                startActivity(
                    Intent(this, FingerprintLockActivity::class.java).apply {
                        putExtra(FingerprintLockConstants.LAUNCHING_MODE, FingerprintLockConstants.ACTIVITY_UNLOCK)
                    },
                )
            }

            config.aafPinLockEnable -> {
                val intent = Intent(this, PinLockActivity::class.java)
                intent.putExtra(PinLockConstants.LAUNCHING_MODE, PinLockConstants.ACTIVITY_UNLOCK)
                startActivity(intent)
            }
        }
    }
}

fun Activity.applyPolicyForRecentApps() {
    if (config.aafPinLockEnable || config.fingerprintLockEnable) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

fun Activity.openGooglePlayBy(targetAppId: String) {
    val uri = Uri.parse("market://details?id=" + targetAppId)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    // To count with Play market backstack, After pressing back button,
    // to taken back to our application, we need to add following flags to intent.
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY or
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
    )
    try {
        startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + targetAppId),
            ),
        )
    }
}

fun Activity.checkWhatsNew(
    releases: List<Release>,
    currVersion: Int,
    applyFilter: Boolean = true,
) {
    when (applyFilter) {
        true -> {
            if (baseConfig.lastVersion == 0) {
                baseConfig.lastVersion = currVersion
                return
            }

            val newReleases = arrayListOf<Release>()
            releases.filterTo(newReleases) { it.id > baseConfig.lastVersion }

            if (newReleases.isNotEmpty() && !baseConfig.avoidWhatsNew) {
                WhatsNewDialog(this, newReleases)
            }

            baseConfig.lastVersion = currVersion
        }

        false -> {
            WhatsNewDialog(this, releases)
        }
    }
}

fun Activity.startActivityWithTransition(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
}

// fun Activity.restartApp() {
//    val readDiaryIntent = Intent(this, DiaryMainActivity::class.java)
//    readDiaryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//    val mPendingIntentId = 123456
//    val mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, readDiaryIntent, PendingIntent.FLAG_CANCEL_CURRENT)
//    val mgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
//    ActivityCompat.finishAffinity(this)
//    //System.runFinalizersOnExit(true)
//    exitProcess(0)
// }

fun Activity.refreshApp() {
    val readDiaryIntent = Intent(this, DiaryMainActivity::class.java)
    readDiaryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    TransitionHelper.startActivityWithTransition(this, readDiaryIntent)
}

fun Activity.startMainActivityWithClearTask() {
    Intent(this, DiaryMainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(this)
    }
    this.overridePendingTransition(0, 0)
}

fun Activity.isAccessFromOutside(): Boolean = intent.getStringExtra(DIARY_EXECUTION_MODE) == EXECUTION_MODE_ACCESS_FROM_OUTSIDE

fun Activity.getLayoutLayoutInflater(): LayoutInflater = getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

fun Activity.scaledDrawable(
    id: Int,
    width: Int,
    height: Int,
): Drawable? {
    var drawable = AppCompatResources.getDrawable(this, id)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = (DrawableCompat.wrap(drawable!!)).mutate()
    }

    val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, width, height, false))
}

// }

// @TargetApi(Build.VERSION_CODES.KITKAT)
// fun Activity.writeFileWithSAF(fileName: String, mimeType: String, requestCode: Int) {
//    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
//        // Filter to only show results that can be "opened", such as
//        // a file (as opposed to a list of contacts or timezones).
//        addCategory(Intent.CATEGORY_OPENABLE)
//
//        type = mimeType
//        // Create a file with the requested MIME type.
//        putExtra(Intent.EXTRA_TITLE, fileName)
//    }
//    startActivityForResult(intent, requestCode)

fun Activity.exportHtmlBook(
    uri: Uri?,
    diaryList: List<Diary>,
) {
    uri?.let {
        val os = contentResolver.openOutputStream(it)
        IOUtils.write(createHtmlString(diaryList), os, "UTF-8")
        os?.close()
    }
}

fun Activity.createHtmlString(diaryList: List<Diary>): String {
    val diaryDivision = StringBuilder()
    diaryList.forEach {
        val html = StringBuilder()
        html.append("<div class='title'> <div class='title-right'>${it.title}</div> </div>")
        html.append(
            "<div class='datetime'>${DateUtils.getDateTimeStringFromTimeMillis(
                it.currentTimeMillis,
                SimpleDateFormat.FULL,
                SimpleDateFormat.FULL,
            )}</div>",
        )
        html.append("<pre class='contents'>")
        html.append(it.contents)
        html.append("</pre>")
        html.append("<div class='photo-container'>")

        it.photoUris?.let { photoUriList ->
            val imageColumn =
                when (photoUriList.size) {
                    1 -> 1

                    //                photoUriList.size % 2 == 0 -> 2
                    else -> 2
                }
            photoUriList.forEach { photoUriDto ->
                html.append(
                    "<div class='photo col$imageColumn'><img src='data:image/png;base64, ${photoToBase64(
                        EasyDiaryUtils.getApplicationDataDirectory(this) + photoUriDto.getFilePath(),
                    )}' /></div>",
                )
            }
        }
        html.append("</div>")
        html.append("<hr>")
        diaryDivision.append(html.toString())
    }

    val template = StringBuilder()
    template.append("<!DOCTYPE html>")
    template.append("<html>")
    template.append("<head>")
    template.append("   <meta charset='UTF-8'>")
    template.append("   <meta name='viewport' content='width=device-width, initial-scale=1.0'>")
    template.append("   <title>Insert title here</title>")
    template.append("   <style type='text/css'>")
    template.append("       body { margin: 1rem; font-family: ë‚˜ëˆ”ê³ ë”•, monospace; }")
    template.append("       hr { margin: 1.5rem 0 }")
    template.append("       .title { margin-top: 1rem; font-size: 1.3rem; display: flex; }")
    template.append("       .title img { width: 30px; margin-right: 1rem; display: block; }")
    template.append("       .title-left { display:inline-block; }")
    template.append("       .title-right { display:inline-block; white-space: pre-wrap; word-break: break-all; }")
    template.append("       .datetime { font-size: 0.8rem; text-align: right; }")
    template.append(
        "       .contents { margin-top: 1rem; font-size: 0.9rem; font-family: ë‚˜ëˆ”ê³ ë”•, monospace; white-space: pre-wrap; word-break: break-all; }",
    )
    template.append("       .photo-container { display: flex; flex-wrap: wrap; }")
    template.append(
        "       .photo-container .photo { background: rgb(240 239 240); padding: 0.3rem; border-radius: 5px; margin: 0.25rem; box-sizing: border-box; }",
    )
    template.append("       .photo.col1 { width: calc(100% - 0.5rem); }")
    template.append("       .photo.col2 { width: calc(50% - 0.5rem); }")
    template.append("       .photo img { width: 100%; display: block; border-radius: 5px; }")
    template.append("   </style>")
    template.append("<body>")
    template.append(diaryDivision.toString())
    template.append("</body>")
    template.append("</html>")

    return template.toString()
}

fun Activity.photoToBase64(photoPath: String): String {
    var image64 = ""
    val bos = ByteArrayOutputStream()
    try {
        val bitmap = BitmapUtils.cropCenter(BitmapFactory.decodeFile(photoPath))
//        val fis = FileInputStream(photoPath)
//        IOUtils.copy(fis, bos)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos)
        image64 = Base64.encodeBase64String(bos.toByteArray())
    } catch (e: Exception) {
        bos.close()
    }
    return image64
}

fun Activity.resourceToBase64(resourceId: Int): String {
    var image64 = ""
    val bitmap = scaledDrawable(resourceId, 100, 100)?.toBitmap()
//        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
    if (bitmap != null) {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
        image64 = Base64.encodeBase64String(bos.toByteArray())
        bos.close()
    }
    return image64
}

fun Activity.openGridSettingDialog(
    rootView: ViewGroup,
    gridSpanMode: GridSpanMode,
    callback: (spanCount: Int) -> Unit,
) {
    var alertDialog: AlertDialog? = null
    val builder = AlertDialog.Builder(this)
    builder.setNegativeButton(getString(android.R.string.cancel), null)
    val inflater = getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val containerView = inflater.inflate(R.layout.dialog_option_item, rootView, false)
    val listView = containerView.findViewById<ListView>(R.id.listView)

    val maxSpanCount =
        when {
            isLandScape() && gridSpanMode == GridSpanMode.DIARY_MAIN -> 5
            !isLandScape() && gridSpanMode == GridSpanMode.DIARY_MAIN -> 3
            isLandScape() && (gridSpanMode == GridSpanMode.POSTCARD || gridSpanMode == GridSpanMode.GALLERY) -> 10
            !isLandScape() && (gridSpanMode == GridSpanMode.POSTCARD || gridSpanMode == GridSpanMode.GALLERY) -> 5
            else -> 2
        }
    val optionItems = mutableListOf<Map<String, String>>()
    for (i in 1..maxSpanCount) {
        optionItems.add(mapOf("optionTitle" to getString(R.string.postcard_grid_option_column_number, i), "optionValue" to "$i"))
    }

    var spanCount = 0
    var selectedIndex = 0
    optionItems.mapIndexed { index, map ->
        val size = map["optionValue"] ?: "0"
        when (isLandScape()) {
            true -> {
                when {
                    gridSpanMode == GridSpanMode.POSTCARD && config.postcardSpanCountLandscape == size.toInt() -> {
                        spanCount = config.postcardSpanCountLandscape
                        selectedIndex = index
                    }

                    gridSpanMode == GridSpanMode.DIARY_MAIN && config.diaryMainSpanCountLandscape == size.toInt() -> {
                        spanCount = config.diaryMainSpanCountLandscape
                        selectedIndex = index
                    }

                    gridSpanMode == GridSpanMode.GALLERY && config.gallerySpanCountLandscape == size.toInt() -> {
                        spanCount = config.gallerySpanCountLandscape
                        selectedIndex = index
                    }
                }
            }

            false -> {
                when {
                    gridSpanMode == GridSpanMode.POSTCARD && config.postcardSpanCountPortrait == size.toInt() -> {
                        spanCount = config.postcardSpanCountPortrait
                        selectedIndex = index
                    }

                    gridSpanMode == GridSpanMode.DIARY_MAIN && config.diaryMainSpanCountPortrait == size.toInt() -> {
                        spanCount = config.diaryMainSpanCountPortrait
                        selectedIndex = index
                    }

                    gridSpanMode == GridSpanMode.GALLERY && config.gallerySpanCountPortrait == size.toInt() -> {
                        spanCount = config.gallerySpanCountPortrait
                        selectedIndex = index
                    }
                }
            }
        }
    }

    val arrayAdapter = OptionItemAdapter(this, R.layout.item_check_label, optionItems, spanCount.toFloat())
    listView.adapter = arrayAdapter
    listView.onItemClickListener =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            @Suppress("UNCHECKED_CAST")
            val optionInfo = parent.adapter.getItem(position) as HashMap<String, String>
            optionInfo["optionValue"]?.let {
//                config.summaryMaxLines = it.toInt()
//                initPreference()
                when (isLandScape()) {
                    true -> {
                        when (gridSpanMode) {
                            GridSpanMode.POSTCARD -> config.postcardSpanCountLandscape = it.toInt()
                            GridSpanMode.DIARY_MAIN -> config.diaryMainSpanCountLandscape = it.toInt()
                            GridSpanMode.GALLERY -> config.gallerySpanCountLandscape = it.toInt()
                        }
                    }

                    false -> {
                        when (gridSpanMode) {
                            GridSpanMode.POSTCARD -> config.postcardSpanCountPortrait = it.toInt()
                            GridSpanMode.DIARY_MAIN -> config.diaryMainSpanCountPortrait = it.toInt()
                            GridSpanMode.GALLERY -> config.gallerySpanCountPortrait = it.toInt()
                        }
                    }
                }
                callback.invoke(it.toInt())
            }
            alertDialog?.cancel()
        }

    alertDialog = builder.create().apply { updateAlertDialog(this, null, containerView, getString(R.string.postcard_grid_option_title)) }
    listView.setSelection(selectedIndex)
}

fun Activity.diaryMainSpanCount(): Int = if (isLandScape()) config.diaryMainSpanCountLandscape else config.diaryMainSpanCountPortrait

fun Activity.postcardViewerSpanCount(): Int = if (isLandScape()) config.postcardSpanCountLandscape else config.postcardSpanCountPortrait

fun Activity.getStatusBarColor(color: Int) = if (config.enableStatusBarDarkenColor) color.darkenColor() else color

fun Activity.updateStatusBarColor(color: Int) {
    window.statusBarColor = getStatusBarColor(color)
}

fun BaseSimpleActivity.acquireGPSPermissions(
    activityResultLauncher: ActivityResultLauncher<Intent>,
    callback: () -> Unit,
) {
    when (
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ).any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission,
            )
        }
    ) {
        true -> {
            // If authorization is denied
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
            startActivity(intent)
        }

        false -> {
            // If authorization request is possible
            handlePermission(PERMISSION_ACCESS_COARSE_LOCATION) { hasCoarseLocation ->
                if (hasCoarseLocation) {
                    handlePermission(PERMISSION_ACCESS_FINE_LOCATION) { hasFineLocation ->
                        if (hasFineLocation) {
                            if (isLocationEnabled()) {
                                callback()
                            } else {
//                        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_ACTION_LOCATION_SOURCE_SETTINGS)
                                activityResultLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        }
                    }
                }
            }
        }
    }
}

// fun EasyDiaryActivity.getLocationWithGPSProvider(callback: (location: Location?) -> Unit) {
//    val gpsProvider = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    val networkProvider = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    when (checkPermission(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION))) {
//        true -> {
//            if (isLocationEnabled()) {
//                callback(gpsProvider.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: networkProvider.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))
//            } else {
//                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_ACTION_LOCATION_SOURCE_SETTINGS)
//            }
//        }
//        false -> {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            handlePermission(PERMISSION_ACCESS_COARSE_LOCATION) { hasCoarseLocation ->
//                if (hasCoarseLocation) {
//                    handlePermission(PERMISSION_ACCESS_FINE_LOCATION) { hasFineLocation ->
//                        if (hasFineLocation) {
//                            if (isLocationEnabled()) {
//                                callback(gpsProvider.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: networkProvider.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))
//                            } else {
//                                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_ACTION_LOCATION_SOURCE_SETTINGS)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
// }


fun Activity.appLaunched() {
    val appId = BuildConfig.APPLICATION_ID
    val defaultClassName = "${appId.removeSuffix(".debug")}.activities.IntroActivity"
    //
    packageManager.setComponentEnabledSetting(
        ComponentName(appId, defaultClassName),
        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
        PackageManager.DONT_KILL_APP,
    )

    val lineClassName = "${appId.removeSuffix(".debug")}.activities.IntroActivity.Line"
    packageManager.setComponentEnabledSetting(
        ComponentName(appId, lineClassName),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP,
    )
}

fun Activity.clearLockSettingsTemporary() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
    val parsedDate: Date? = dateFormat.parse("2022-05-14 23:59:59")
    parsedDate?.let {
        val remainMinutes =
            it.time
                .minus(System.currentTimeMillis())
                .div(1000)
                .div(60)
        if (remainMinutes > 0) {
            config.aafPinLockEnable = false
            config.fingerprintLockEnable = false
            showAlertDialog(
                "Password lock setting is forcibly released. Password lock settings will be unavailable for the next $remainMinutes minutes.",
                null,
            )
        }
    }
}

fun Activity.uriToFile(
    uri: Uri,
    photoPath: String,
): Boolean {
    var result = false
    try {
        val tempFile = File.createTempFile("TEMP_PHOTO", "AAF").apply { deleteOnExit() }
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(tempFile)
        IOUtils.copy(inputStream, outputStream)
        IOUtils.closeQuietly(inputStream)
        IOUtils.closeQuietly(outputStream)

        val compressedFile = Compressor(this).setQuality(70).compressToFile(tempFile)
        FileUtils.copyFile(compressedFile, File(photoPath))
        result = true
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

fun Activity.triggerRestart(cls: Class<*>) {
    val intent = Intent(this, cls)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
    finish()
    Runtime.getRuntime().exit(0)
}

@SuppressLint("SourceLockedOrientationActivity")
fun Activity.holdCurrentOrientation() {
    when (resources.configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Configuration.ORIENTATION_LANDSCAPE -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}

fun Activity.clearHoldOrientation() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

fun Activity.hideSoftInputFromWindow() {
    val currentView = this.currentFocus
    if (currentView != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentView.windowToken, 0)
    }
}


fun Activity.startReviewFlow() {
    config.appExecutionCount = 0
}

fun Activity.openGoogleMap(location: com.quangthe.nhatky.models.Location) {
    val lat = location.latitude
    val lng = location.longitude
    val label = location.address ?: "Vá»‹ trÃ­ cá»§a tÃ´i"

    // Táº¡o URI: geo:lat,long?q=lat,long(Label)
    val uri = Uri.parse("geo:0,0?q=$lat,$lng(${Uri.encode(label)})")

    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
    try {
        startActivity(mapIntent)
    } catch (e: ActivityNotFoundException) {
        // Náº¿u mÃ¡y khÃ´ng cÃ³ app báº£n Ä‘á»“, má»Ÿ báº±ng trÃ¬nh duyá»‡t (fallback)
        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
        startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}
