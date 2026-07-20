package com.quangthe.nhatky.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.simplemobiletools.commons.models.Release
import com.quangthe.nhatky.core.config.DIARY_EXECUTION_MODE
import com.quangthe.nhatky.core.config.EXECUTION_MODE_ACCESS_FROM_OUTSIDE
import com.quangthe.nhatky.dialogs.WhatsNewDialog
import com.simplemobiletools.commons.extensions.baseConfig

fun Activity.isAccessFromOutside(): Boolean = intent.getStringExtra(DIARY_EXECUTION_MODE) == EXECUTION_MODE_ACCESS_FROM_OUTSIDE

fun Activity.getLayoutLayoutInflater(): LayoutInflater = getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
