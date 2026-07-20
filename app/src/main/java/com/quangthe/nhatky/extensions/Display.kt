package com.quangthe.nhatky.extensions

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.quangthe.nhatky.R

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
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        Point(bounds.width(), bounds.height())
    } else {
        val display = @Suppress("DEPRECATION") windowManager.defaultDisplay
        val size = Point()
        @Suppress("DEPRECATION")
        display.getSize(size)
        size
    }
}

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

fun Activity.getDisplayMetrics(): DisplayMetrics {
    val outMetrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val display = display
        @Suppress("DEPRECATION")
        display?.getMetrics(outMetrics)
    } else {
        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getMetrics(outMetrics)
    }
    return outMetrics
}
