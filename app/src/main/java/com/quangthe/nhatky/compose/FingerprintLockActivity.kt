package com.quangthe.nhatky.compose

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.holdCurrentOrientation
import com.quangthe.nhatky.extensions.isLandScape
import com.quangthe.nhatky.extensions.hideSystemBars
import com.quangthe.nhatky.extensions.pauseLock
import com.quangthe.nhatky.extensions.showAlertDialog
import com.quangthe.nhatky.helper.FingerprintLockConstants
import com.quangthe.nhatky.helper.PinLockConstants
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class FingerprintLockActivity : EasyDiaryComposeBaseActivity() {
    private lateinit var mKeyStore: KeyStore
    private lateinit var mKeyGenerator: KeyGenerator
    private var mActivityMode: String? = null
    private var mSettingComplete = false
    private var mErrorMessageState = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivityMode = intent.getStringExtra(FingerprintLockConstants.LAUNCHING_MODE)

        if (isLandScape()) hideSystemBars()

        setContent {
            FingerprintLockScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mSettingComplete) {
            startBiometricAuth()
        }
    }

    @Composable
    private fun FingerprintLockScreen() {
        val errorMessage by mErrorMessageState

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_fingerprint),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.place_finger_description),
                textAlign = TextAlign.Center,
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    textAlign = TextAlign.Center,
                )
            }

            if (mActivityMode != FingerprintLockConstants.ACTIVITY_SETTING) {
                Spacer(Modifier.height(24.dp))
                Button(onClick = {
                    startActivity(
                        Intent(this@FingerprintLockActivity, PinLockActivity::class.java).apply {
                            putExtra(PinLockConstants.LAUNCHING_MODE, PinLockConstants.ACTIVITY_UNLOCK)
                        },
                    )
                    finish()
                }) {
                    Text("Use PIN instead")
                }
            }
        }
    }

    private fun startBiometricAuth() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    config.fingerprintAuthenticationFailCount = 0
                    val cipher = result.cryptoObject?.cipher

                    when (mActivityMode) {
                        FingerprintLockConstants.ACTIVITY_SETTING -> {
                            tryEncrypt(cipher)
                            holdCurrentOrientation()
                            mSettingComplete = true
                            showAlertDialog(
                                getString(R.string.fingerprint_setting_complete),
                                { _, _ ->
                                    config.fingerprintLockEnable = true
                                    pauseLock()
                                    finish()
                                },
                                false,
                            )
                        }

                        FingerprintLockConstants.ACTIVITY_UNLOCK -> {
                            if (tryDecrypt(cipher)) {
                                pauseLock()
                                finish()
                            }
                        }
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        mErrorMessageState.value = errString.toString()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    mErrorMessageState.value = getString(R.string.fingerprint_authentication_fail_try_again)
                }
            },
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.app_name))
            .setSubtitle(getString(R.string.place_finger_description))
            .setNegativeButtonText(getString(R.string.cancel))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

            val defaultCipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
            val keyguardManager = getSystemService(KeyguardManager::class.java)

            if (!keyguardManager.isKeyguardSecure) {
                mErrorMessageState.value = "Secure lock screen hasn't set up."
                return
            }

            if (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                if (mActivityMode == FingerprintLockConstants.ACTIVITY_SETTING) {
                    createKey(FingerprintLockConstants.KEY_NAME, true)
                }
                if (initCipher(defaultCipher, FingerprintLockConstants.KEY_NAME)) {
                    biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(defaultCipher))
                }
            } else {
                mErrorMessageState.value = "Fingerprint not registered or hardware not available."
            }
        } catch (e: Exception) {
            mErrorMessageState.value = "Initialization error: ${e.message}"
        }
    }

    private fun createKey(keyName: String, invalidatedByBiometricEnrollment: Boolean) {
        try {
            mKeyStore.load(null)
            val builder = KeyGenParameterSpec.Builder(
                keyName,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)
            }
            mKeyGenerator.init(builder.build())
            mKeyGenerator.generateKey()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun initCipher(cipher: Cipher, keyName: String): Boolean {
        return try {
            mKeyStore.load(null)
            val key = mKeyStore.getKey(keyName, null) as SecretKey
            if (mActivityMode == FingerprintLockConstants.ACTIVITY_SETTING) {
                cipher.init(Cipher.ENCRYPT_MODE, key)
            } else {
                val iv = Base64.decode(config.fingerprintEncryptDataIV, Base64.DEFAULT)
                cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            }
            true
        } catch (e: Exception) {
            mErrorMessageState.value = getString(R.string.init_cipher_error_guide_message)
            false
        }
    }

    private fun tryEncrypt(cipher: Cipher?) {
        try {
            cipher?.let {
                val encrypted = it.doFinal(FingerprintLockConstants.DUMMY_ENCRYPT_DATA.toByteArray(Charsets.UTF_8))
                val ivParams = it.parameters.getParameterSpec(IvParameterSpec::class.java)
                config.fingerprintEncryptData = Base64.encodeToString(encrypted, Base64.DEFAULT)
                config.fingerprintEncryptDataIV = Base64.encodeToString(ivParams.iv, Base64.DEFAULT)
            }
        } catch (_: Exception) {}
    }

    private fun tryDecrypt(cipher: Cipher?): Boolean {
        return try {
            cipher?.let {
                val encodedData = Base64.decode(config.fingerprintEncryptData, Base64.DEFAULT)
                it.doFinal(encodedData)
                true
            } ?: false
        } catch (e: Exception) {
            mErrorMessageState.value = getString(R.string.fingerprint_authentication_info_changed)
            false
        }
    }
}
