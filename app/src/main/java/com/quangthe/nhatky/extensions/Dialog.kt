package com.quangthe.nhatky.extensions

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.quangthe.nhatky.commons.utils.FontUtils
import com.quangthe.nhatky.databinding.PartialDialogTitleBinding
import com.quangthe.nhatky.enums.DialogMode
import com.quangthe.nhatky.R

fun Context.showAlertDialog(
    message: String,
    positiveListener: DialogInterface.OnClickListener?,
    negativeListener: DialogInterface.OnClickListener?,
    dialogMode: DialogMode = DialogMode.DEFAULT,
    cancelable: Boolean = true,
    paramTitle: String? = null,
    positiveButtonLabel: String = getString(R.string.ok),
    negativeButtonLabel: String = getString(R.string.cancel),
) {
    showAlertDialog(
        message,
        positiveListener,
        negativeListener,
        null,
        dialogMode,
        cancelable,
        paramTitle,
        positiveButtonLabel,
        negativeButtonLabel,
    )
}

fun Context.showAlertDialog(
    message: String,
    positiveListener: DialogInterface.OnClickListener?,
    negativeListener: DialogInterface.OnClickListener?,
    neutralListener: DialogInterface.OnClickListener?,
    dialogMode: DialogMode = DialogMode.DEFAULT,
    cancelable: Boolean = true,
    paramTitle: String? = null,
    positiveButtonLabel: String = getString(R.string.ok),
    negativeButtonLabel: String = getString(R.string.cancel),
    neutralButtonLabel: String = "-",
) {
    var iconResourceId: Int? = null
    var title: String? = null
    when (dialogMode) {
        DialogMode.INFO -> {
            title = getString(R.string.ok)
            iconResourceId = R.drawable.ic_info
        }

        DialogMode.WARNING -> {
            title = "WARNING"
            iconResourceId = R.drawable.ic_warning
        }

        DialogMode.ERROR -> {
            title = "ERROR"
            iconResourceId = R.drawable.ic_error
        }

        DialogMode.SETTING -> {
            title = getString(R.string.ok)
            iconResourceId = R.drawable.ic_settings_7
        }

        DialogMode.DEFAULT -> {
            title = getString(R.string.app_name)
            iconResourceId = R.drawable.ic_easydiary
        }
    }

    val builder = AlertDialog.Builder(this)
    builder.setCancelable(cancelable)
    builder.setPositiveButton(positiveButtonLabel, positiveListener)
    negativeListener?.let { builder.setNegativeButton(negativeButtonLabel, it) }
    neutralListener?.let { builder.setNeutralButton(neutralButtonLabel, it) }
    builder.create().apply {
        updateAlertDialog(this, message, null, paramTitle ?: title, 255, iconResourceId)
    }
}

@Deprecated(
    message = "Legacy function",
    replaceWith =
        ReplaceWith(
            "showAlertDialogWithIcon()",
            "com.quangthe.nhatky.extensions.Context",
        ),
)
fun Context.showAlertDialog(
    message: String,
    positiveListener: DialogInterface.OnClickListener?,
    cancelable: Boolean = true,
) {
    showAlertDialog(message, positiveListener, null, DialogMode.INFO, cancelable)
}

fun Context.showAlertDialog(message: String) {
    showAlertDialog(message, null, null, DialogMode.INFO, true)
}

fun Context.updateAlertDialogWithIcon(
    dialogMode: DialogMode,
    alertDialog: AlertDialog,
    message: String? = null,
    customView: View? = null,
    customTitle: String? = null,
    backgroundAlpha: Int = 255,
) {
    var title: String? = null
    var iconResourceId: Int? = null
    when (dialogMode) {
        DialogMode.INFO -> {
            title = getString(R.string.ok)
            iconResourceId = R.drawable.ic_info
        }

        DialogMode.SETTING -> {
            title = getString(R.string.settings)
            iconResourceId = R.drawable.ic_settings_7
        }

        else -> {}
    }

    updateAlertDialog(
        alertDialog,
        message,
        customView,
        customTitle ?: title,
        backgroundAlpha,
        iconResourceId,
    )
}

fun Context.updateAlertDialog(
    alertDialog: AlertDialog,
    message: String? = null,
    customView: View? = null,
    customTitle: String? = null,
    backgroundAlpha: Int = 255,
    customTitleIcon: Int? = null,
) {
    alertDialog.run {
        if (customView != null) {
            setView(customView)
        }
        if (!isNightMode()) {
            window?.setBackgroundDrawable(
                GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(config.backgroundColor)
                    cornerRadius = dpToPixelFloatValue(3F)
                    alpha = backgroundAlpha
                },
            )
        }

        val globalTypeface = FontUtils.getCommonTypeface(this@updateAlertDialog)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        PartialDialogTitleBinding.inflate(layoutInflater).apply {
            textDialogTitle.run {
                text = customTitle ?: getString(R.string.app_name)
                if (!isNightMode()) setTextColor(Color.WHITE)
                typeface = globalTypeface
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18F)
            }
            customTitleIcon?.let {
                imgDialogTitle.run {
                    visibility = View.VISIBLE
                    setImageDrawable(ContextCompat.getDrawable(this@updateAlertDialog, it))
                    changeDrawableIconColor(Color.WHITE, this)
                }
            }
            setCustomTitle(this.root)
        }
        show()
        getButton(AlertDialog.BUTTON_POSITIVE).run {
            if (!isNightMode()) setTextColor(config.textColor)
            typeface = globalTypeface
        }
        getButton(AlertDialog.BUTTON_NEGATIVE).run {
            if (!isNightMode()) setTextColor(config.textColor)
            typeface = globalTypeface
        }
        if (!isNightMode()) getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(config.textColor)
    }
}
