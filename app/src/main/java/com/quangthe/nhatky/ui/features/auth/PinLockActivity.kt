package com.quangthe.nhatky.ui.features.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quangthe.nhatky.ui.base.EasyDiaryComposeBaseActivity
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.quangthe.nhatky.R
import com.quangthe.nhatky.ui.features.auth.FingerprintLockActivity
import com.quangthe.nhatky.enums.DialogMode
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.isLandScape
import com.quangthe.nhatky.extensions.hideSystemBars
import com.quangthe.nhatky.extensions.holdCurrentOrientation
import com.quangthe.nhatky.extensions.pauseLock
import com.quangthe.nhatky.extensions.showAlertDialog
import com.quangthe.nhatky.core.config.FingerprintLockConstants
import com.quangthe.nhatky.core.config.PinLockConstants

class PinLockActivity : EasyDiaryComposeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityMode = intent.getStringExtra(PinLockConstants.LAUNCHING_MODE)

        if (isLandScape()) hideSystemBars()

        setContent {
            PinLockScreen(activityMode)
        }
    }

    @Composable
    private fun PinLockScreen(activityMode: String?) {
        val isSettingMode = activityMode == PinLockConstants.ACTIVITY_SETTING
        var cursorIndex by remember { mutableIntStateOf(0) }
        val password = remember { arrayOfNulls<String>(4) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(config.primaryColor))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(40.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                for (i in 0..3) {
                    val char = password[i]
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (char != null) Color.White else Color.White.copy(alpha = 0.3f)
                            ),
                    )
                }
            }

            val iconSize = if (LocalConfiguration.current.screenWidthDp < 360) 40.dp else 60.dp
            Spacer(Modifier.height(24.dp))
            Icon(
                painter = painterResource(R.drawable.ic_intro),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconSize),
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = if (isSettingMode) stringResource(R.string.pin_setting_guide_message)
                       else stringResource(R.string.pin_unlock_guide_message),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.weight(1f))

            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("fingerprint", "0", "delete"),
            )

            for (row in keys) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    for (key in row) {
                        val showFingerprint = key == "fingerprint" && config.fingerprintLockEnable
                        if (showFingerprint || key != "fingerprint") {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .width(80.dp)
                                    .aspectRatio(1.5f)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (key == "delete") {
                                            if (cursorIndex > 0) {
                                                cursorIndex--
                                                password[cursorIndex] = null
                                            }
                                        } else if (key == "fingerprint") {
                                            val intent = Intent(
                                                this@PinLockActivity,
                                                FingerprintLockActivity::class.java,
                                            )
                                            intent.putExtra(
                                                FingerprintLockConstants.LAUNCHING_MODE,
                                                FingerprintLockConstants.ACTIVITY_UNLOCK,
                                            )
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            if (cursorIndex < 4) {
                                                password[cursorIndex] = key
                                                cursorIndex++
                                                if (cursorIndex == 4) {
                                                    val fullPassword = password.joinToString("")
                                                    onPasswordComplete(fullPassword, isSettingMode, activityMode)
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (key == "fingerprint") {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp),
                                    )
                                } else if (key == "delete") {
                                    Text("←", color = Color.White, fontSize = 24.sp)
                                } else {
                                    Text(key, color = Color.White, fontSize = 24.sp)
                                }
                            }
                        } else {
                            Spacer(Modifier.width(88.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    private fun onPasswordComplete(fullPassword: String, isSettingMode: Boolean, activityMode: String?) {
        if (isSettingMode) {
            holdCurrentOrientation()
            showAlertDialog(
                getString(R.string.pin_setting_complete, fullPassword),
                { _, _ ->
                    config.aafPinLockEnable = true
                    config.aafPinLockSavedPassword = fullPassword
                    pauseLock()
                    finish()
                },
                { _, _ ->
                    finish()
                },
                DialogMode.INFO,
                false,
            )
        } else if (activityMode == PinLockConstants.ACTIVITY_UNLOCK) {
            if (config.aafPinLockSavedPassword == fullPassword) {
                pauseLock()
                finish()
            } else {
                holdCurrentOrientation()
                showAlertDialog(
                    message = getString(R.string.pin_verification_fail),
                    positiveListener = { _, _ ->
                        ActivityCompat.finishAffinity(this@PinLockActivity)
                    },
                    negativeListener = null,
                    dialogMode = DialogMode.DEFAULT,
                    cancelable = false,
                )
            }
        }
    }
}
