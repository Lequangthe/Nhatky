package com.quangthe.nhatky.services

import android.app.IntentService
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.simplemobiletools.commons.extensions.toast
import com.quangthe.nhatky.helper.NOTIFICATION_COMPRESS_ID
import com.quangthe.nhatky.helper.NOTIFICATION_DECOMPRESS_ID
import com.quangthe.nhatky.helper.NOTIFICATION_ID
import com.quangthe.nhatky.helper.NotificationConstants

open class BaseNotificationService(
    name: String = "EasyDiaryNotificationService",
) : IntentService(name) {
    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            when (it.action) {
                NotificationConstants.ACTION_DISMISS_COMPRESS -> {
                    NotificationManagerCompat.from(applicationContext).cancel(NOTIFICATION_COMPRESS_ID)
                }

                NotificationConstants.ACTION_DISMISS_DECOMPRESS -> {
                    NotificationManagerCompat.from(applicationContext).cancel(NOTIFICATION_DECOMPRESS_ID)
                }

                NotificationConstants.ACTION_DEV_DISMISS -> {
                    NotificationManagerCompat.from(applicationContext).cancel(intent.getIntExtra(NOTIFICATION_ID, 0))
                }

                NotificationConstants.ACTION_DEV_TOAST -> {
                    NotificationManagerCompat.from(applicationContext).cancel(intent.getIntExtra(NOTIFICATION_ID, 0))
                    applicationContext.toast("Notification ID: ${intent.getIntExtra(NOTIFICATION_ID, 0)}")
                }
            }
        }
    }
}
