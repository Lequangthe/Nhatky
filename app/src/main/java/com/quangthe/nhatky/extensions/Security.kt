package com.quangthe.nhatky.extensions

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.quangthe.nhatky.BuildConfig
import com.quangthe.nhatky.R
import com.quangthe.nhatky.activities.BaseSimpleActivity
import com.quangthe.nhatky.core.config.FingerprintLockConstants
import com.quangthe.nhatky.core.config.PERMISSION_ACCESS_COARSE_LOCATION
import com.quangthe.nhatky.core.config.PERMISSION_ACCESS_FINE_LOCATION
import com.quangthe.nhatky.core.config.PinLockConstants
import com.quangthe.nhatky.ui.features.auth.FingerprintLockActivity
import com.quangthe.nhatky.ui.features.auth.PinLockActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

fun Activity.appLaunched() {
    val appId = BuildConfig.APPLICATION_ID
    val defaultClassName = "${appId.removeSuffix(".debug")}.activities.IntroActivity"
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
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
            startActivity(intent)
        }

        false -> {
            handlePermission(PERMISSION_ACCESS_COARSE_LOCATION) { hasCoarseLocation ->
                if (hasCoarseLocation) {
                    handlePermission(PERMISSION_ACCESS_FINE_LOCATION) { hasFineLocation ->
                        if (hasFineLocation) {
                            if (isLocationEnabled()) {
                                callback()
                            } else {
                                activityResultLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        }
                    }
                }
            }
        }
    }
}
