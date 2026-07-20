package com.quangthe.nhatky.activities

import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.getPermissionString
import com.quangthe.nhatky.extensions.getThemeId
import com.quangthe.nhatky.extensions.hasPermission
import com.quangthe.nhatky.extensions.pauseLock
import com.quangthe.nhatky.extensions.updateStatusBarColor
import com.quangthe.nhatky.core.config.SettingConstants
import com.quangthe.nhatky.core.navigation.TransitionHelper

/**
 * Created by CHO HANJOONG on 2017-11-25.
 * This code based 'Simple-Commons' package
 * You can see original 'Simple-Commons' from below link.
 * https://github.com/SimpleMobileTools/Simple-Commons
 */

open class BaseSimpleActivity : AppCompatActivity() {
    /***************************************************************************************************
     *   global properties
     *
     ***************************************************************************************************/
    var actionOnPermission: ((granted: Boolean) -> Unit)? = null
    var isAskingPermissions = false
    var useDynamicTheme = true

    /***************************************************************************************************
     *   override functions
     *
     ***************************************************************************************************/
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (useDynamicTheme) {
            setTheme(getThemeId())
//            setTheme(R.style.AppTheme_AAF)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (useDynamicTheme) {
            setTheme(getThemeId())
            updateBackgroundColor(config.screenBackgroundColor)
        }
        updateActionbarColor()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finishActivityWithPauseLock()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isAskingPermissions = false
        if (requestCode == SettingConstants.GENERIC_PERM_HANDLER && grantResults.isNotEmpty()) {
            actionOnPermission?.invoke(grantResults[0] == 0)
        }
    }

    /***************************************************************************************************
     *   etc functions
     *
     ***************************************************************************************************/
    open fun updateBackgroundColor(color: Int = config.screenBackgroundColor) {
        val mainView: ViewGroup? = getMainViewGroup()
        mainView?.run {
//            setBackgroundColor(ColorUtils.setAlphaComponent(color, 255))
            setBackgroundColor(color)
        }
    }

    open fun getMainViewGroup(): ViewGroup? = null
    //    open fun getBackgroundAlpha(): Int = 255

    fun updateActionbarColor(color: Int = config.primaryColor) {
        supportActionBar?.setBackgroundDrawable(color.toDrawable())
//        supportActionBar?.title = Html.fromHtml("<font color='${color.getContrastColor().toHex()}'>${supportActionBar?.title}</font>")
        updateStatusBarColor(color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val taskDescription =
                ActivityManager.TaskDescription
                    .Builder()
                    .setPrimaryColor(color)
                    .build()
            setTaskDescription(taskDescription)
        } else {
            @Suppress("DEPRECATION")
            setTaskDescription(ActivityManager.TaskDescription(null, null, color))
        }
    }

    fun handlePermission(
        permissionId: Int,
        callback: (granted: Boolean) -> Unit,
    ) {
        actionOnPermission = null
        if (hasPermission(permissionId)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(
                this,
                arrayOf(getPermissionString(permissionId)),
                SettingConstants.GENERIC_PERM_HANDLER,
            )
        }
    }

    protected fun finishActivityWithPauseLock() {
        pauseLock()
        TransitionHelper.finishActivityWithTransition(this@BaseSimpleActivity)
    }
}
