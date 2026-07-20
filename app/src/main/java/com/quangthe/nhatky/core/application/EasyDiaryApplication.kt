package com.quangthe.nhatky.core.application

import android.content.Context
import androidx.multidex.MultiDexApplication

class EasyDiaryApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        var context: Context? = null
    }
}
