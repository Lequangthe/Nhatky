package com.quangthe.nhatky.extensions

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.quangthe.nhatky.BuildConfig
import com.quangthe.nhatky.enums.Launcher

val themeItems = listOf(Launcher.EASY_DIARY, Launcher.DARK, Launcher.GREEN, Launcher.DEBUG)

fun Context.toggleLauncher(launcher: Launcher) {
    themeItems.forEach {
        checkAppIconColor(it.themeName, it == launcher)
    }
}

fun Context.checkAppIconColor(
    colorName: String,
    enable: Boolean = false,
) {
    val appId = BuildConfig.APPLICATION_ID
    toggleAppIconColor(appId, -1, -1, enable, colorName)
}

fun Context.toggleAppIconColor(
    appId: String,
    colorIndex: Int,
    color: Int,
    enable: Boolean,
    colorName: String,
) {
    val className = "${appId.removeSuffix(".debug")}.activities.DiaryMainActivity.$colorName"
    val state = if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    try {
        packageManager.setComponentEnabledSetting(ComponentName(appId, className), state, PackageManager.DONT_KILL_APP)
    } catch (e: Exception) {
    }
}
