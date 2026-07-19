package com.quangthe.nhatky.helper

import android.content.Context
import androidx.multidex.MultiDexApplication

/**
 * Created by CHO HANJOONG on 2017-03-16.
 */

class EasyDiaryApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        var context: Context? = null
    }
}
