package com.quangthe.nhatky.extensions

import android.app.PendingIntent
import android.content.Context
import android.os.Build

fun Context.pendingIntentFlag() =
    if (Build.VERSION.SDK_INT >=
        Build.VERSION_CODES.M
    ) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }

fun Context.pendingIntentFlagMutable() =
    if (Build.VERSION.SDK_INT >=
        Build.VERSION_CODES.M
    ) {
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
