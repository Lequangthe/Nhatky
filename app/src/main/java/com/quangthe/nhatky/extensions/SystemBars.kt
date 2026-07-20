package com.quangthe.nhatky.extensions

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat


fun Activity.hideSystemBarsVanillaIceCreamPlusIsLandScape() {
    if (isVanillaIceCreamPlus() && isLandScape()) {
        hideSystemBars()
    }
}

fun Activity.hideSystemBars() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        window.insetsController?.hide(WindowInsets.Type.systemBars())
        window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Activity.applyFullScreenStatusBarTheme(checkColor: Int = config.screenBackgroundColor) {
    if (isVanillaIceCreamPlus()) {
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

fun Activity.getStatusBarColor(color: Int) = if (config.enableStatusBarDarkenColor) color.darkenColor() else color

fun Activity.updateStatusBarColor(color: Int) {
    window.statusBarColor = getStatusBarColor(color)
}
