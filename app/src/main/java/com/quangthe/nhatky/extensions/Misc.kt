package com.quangthe.nhatky.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.os.PowerManager

fun isBelowVanillaIceCream() = Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM

fun isVanillaIceCreamPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM

fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

fun isRedVelvetCakePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun Context.isScreenOn(): Boolean {
    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isInteractive
}

fun Context.isColorLight(color: Int): Boolean {
    val r = Color.red(color)
    val g = Color.green(color)
    val b = Color.blue(color)

    val brightness = (0.299 * r + 0.587 * g + 0.114 * b)
    return brightness > 128
}

tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
