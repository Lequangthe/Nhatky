package com.quangthe.nhatky.core.navigation

import android.app.Activity
import android.content.Intent
import com.quangthe.nhatky.R
import com.quangthe.nhatky.core.config.TransitionConstants

class TransitionHelper {
    companion object {
        fun startActivityWithTransition(
            activity: Activity?,
            intent: Intent,
            type: Int = TransitionConstants.DEFAULT,
        ) {
            activity?.run {
                startActivity(intent)
                when (type) {
                    TransitionConstants.DEFAULT -> overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    TransitionConstants.BOTTOM_TO_TOP -> overridePendingTransition(R.anim.slide_in_up, R.anim.stay)
                }
            }
        }

        fun finishActivityWithTransition(
            activity: Activity?,
            type: Int = TransitionConstants.DEFAULT,
        ) {
            activity?.run {
                finish()
                when (type) {
                    TransitionConstants.DEFAULT -> overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    TransitionConstants.TOP_TO_BOTTOM -> overridePendingTransition(R.anim.stay, R.anim.slide_in_down)
                }
            }
        }
    }
}
