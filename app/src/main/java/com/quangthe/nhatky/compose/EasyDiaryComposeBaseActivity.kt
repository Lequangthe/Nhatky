package com.quangthe.nhatky.compose

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.quangthe.nhatky.activities.BaseSimpleActivity
import com.quangthe.nhatky.extensions.*
import com.quangthe.nhatky.helper.TransitionHelper
import com.quangthe.nhatky.viewmodels.SettingsViewModel

open class EasyDiaryComposeBaseActivity :
    BaseSimpleActivity() {
    val mSettingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isBelowVanillaIceCream()) {
            @Suppress("DEPRECATION")
            window.statusBarColor = getStatusBarColor(config.primaryColor)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishActivityWithTransition()
                }
            },
        )
    }

    override fun onResume() {
        super.onResume()

        resumeLock()
        applyPolicyForRecentApps()
    }

    override fun onPause() {
        super.onPause()

        pauseLock()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    fun finishActivityWithTransition() {
        TransitionHelper.finishActivityWithTransition(this@EasyDiaryComposeBaseActivity)
    }
}
