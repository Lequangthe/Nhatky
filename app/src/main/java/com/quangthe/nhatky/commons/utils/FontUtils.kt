package com.quangthe.nhatky.commons.utils

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.text.font.FontFamily
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.preferencesContains
import com.quangthe.nhatky.extensions.spToPixelFloatValue
import com.quangthe.nhatky.helper.*
import com.quangthe.nhatky.views.FixedTextView

/**
 * Created by CHO HANJOONG on 2017-03-16.
 */
object FontUtils {

    private fun setTypeface(context: Context, viewGroup: ViewGroup, typeface: Typeface?, customLineSpacing: Boolean) {
        for (i in 0 until viewGroup.childCount) {
            when (val targetView = viewGroup.getChildAt(i)) {
                is ViewGroup -> setTypeface(context, targetView, typeface, customLineSpacing)
                is TextView -> {
                    targetView.also {
                        it.typeface = typeface
                        if (customLineSpacing) {
                            it.setLineSpacing(0F, context.config.lineSpacingScaleFactor)
                        }

                        if (it is FixedTextView && it.applyHighLight) EasyDiaryUtils.highlightString(it)
                        if (it is FixedTextView && it.applyBoldStyle) it.setTypeface(typeface, Typeface.BOLD)
                    }
                }
                else -> {}
            }
        }
    }

    fun getCommonTypeface(context: Context): Typeface? {
        return Typeface.DEFAULT
    }

    fun setTypefaceDefault(view: TextView) {
        view.typeface = Typeface.DEFAULT
    }

    fun setCommonTypeface(context: Context) {
        // No-op
    }

    fun setFontsTypeface(context: Context, customFontName: String?, rootView: ViewGroup?, customLineSpacing: Boolean = true) {
        rootView?.let {
            setTypeface(context, it, Typeface.DEFAULT, customLineSpacing)
        }
    }

    fun getTypeface(context: Context, fontName: String?): Typeface? {
        return Typeface.DEFAULT
    }

    fun measureTextWidth(context: Context, paint: Paint, text: String, scaleFactor: Float = 1.9f): Int = paint.apply {
        typeface = Typeface.DEFAULT
    }.measureText(text).toInt().times(scaleFactor).toInt()

    fun checkFontSetting(activity: Activity) {
        activity.run {
            // Initial font size setting
            if (!preferencesContains(SETTING_FONT_SIZE)) {
                config.settingFontSize = spToPixelFloatValue(UN_SUPPORT_LANGUAGE_FONT_SIZE_DEFAULT_SP.toFloat())
            }
        }
    }

    fun isDeviceSettingFont(context: Context): Boolean {
        return true
    }

    fun getComposeFontFamily(context: Context): FontFamily? {
        return null
    }
}
