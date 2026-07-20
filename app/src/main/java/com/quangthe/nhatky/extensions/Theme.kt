package com.quangthe.nhatky.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.TypedValue
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.simplemobiletools.commons.extensions.adjustAlpha
import com.simplemobiletools.commons.extensions.isBlackAndWhiteTheme
import com.simplemobiletools.commons.views.MyAppCompatSpinner
import com.simplemobiletools.commons.views.MyButton
import com.simplemobiletools.commons.views.MyEditText
import com.simplemobiletools.commons.views.MyFloatingActionButton
import com.simplemobiletools.commons.views.MySeekBar
import com.simplemobiletools.commons.views.MySwitchCompat
import com.simplemobiletools.commons.views.MyTextView
import com.quangthe.nhatky.core.config.SUPPORT_LANGUAGE_FONT_SIZE_DEFAULT_SP
import com.quangthe.nhatky.R
import com.quangthe.nhatky.views.FixedCardView
import com.quangthe.nhatky.views.FixedTextView
import com.quangthe.nhatky.views.ItemCardView

fun Context.isNightMode() =
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> false
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }

fun Context.pauseLock() {
    if (config.aafPinLockEnable || config.fingerprintLockEnable) {
        config.aafPinLockPauseMillis = System.currentTimeMillis()
    }
}

fun Context.updateTextColors(
    viewGroup: ViewGroup,
    tmpTextColor: Int = 0,
    tmpAccentColor: Int = 0,
) {
    if (isNightMode()) return

    val textColor = if (tmpTextColor == 0) config.textColor else tmpTextColor
    val backgroundColor = config.backgroundColor
    val accentColor =
        if (tmpAccentColor == 0) {
            if (isBlackAndWhiteTheme()) {
                Color.WHITE
            } else {
                config.primaryColor
            }
        } else {
            tmpAccentColor
        }

    val cnt = viewGroup.childCount
    (0 until cnt)
        .map { viewGroup.getChildAt(it) }
        .forEach {
            when (it) {
                is MyTextView -> {
                    it.setColors(textColor, accentColor, backgroundColor)
                }

                is FixedTextView -> {
                    if (it.applyGlobalColor) it.setColors(textColor, accentColor, backgroundColor)
                }

                is MyAppCompatSpinner -> {
                    it.setColors(textColor, accentColor, backgroundColor)
                }

                is MySwitchCompat -> {
                    it.setColors(textColor, accentColor, backgroundColor)
                }

                is MyEditText -> {
                    it.setTextColor(textColor)
                    it.setHintTextColor(textColor.adjustAlpha(0.5f))
                    it.setLinkTextColor(accentColor)
                }

                is MyFloatingActionButton -> {
                    it.backgroundTintList = ColorStateList.valueOf(accentColor)
                }

                is MySeekBar -> {
                    it.setColors(textColor, accentColor, backgroundColor)
                }

                is MyButton -> {
                    it.setColors(textColor, accentColor, backgroundColor)
                }

                is ViewGroup -> {
                    updateTextColors(it, textColor, accentColor)
                }
            }
        }
}

fun Context.innerCardDarkenFactor(): Int = if (isColorLight(config.backgroundColor)) 2 else 2.unaryMinus()

fun Context.updateDashboardInnerCard(cardView: CardView) {
    cardView.setCardBackgroundColor(config.backgroundColor.darkenColor(innerCardDarkenFactor()))
}

fun Context.updateAppViews(
    viewGroup: ViewGroup,
    tmpBackgroundColor: Int = 0,
) {
    if (isNightMode()) return

    val backgroundColor = if (tmpBackgroundColor == 0) config.backgroundColor else tmpBackgroundColor
    val cnt = viewGroup.childCount
    (0 until cnt)
        .map { viewGroup.getChildAt(it) }
        .forEach {
            when (it) {
                is CardView -> {
                    when (it is FixedCardView) {
                        true -> {
                            if (it.applyCardBackgroundColor) it.setCardBackgroundColor(backgroundColor)
                            if (it.dashboardInnerCard) {
                                updateDashboardInnerCard(it)
                            }
                        }

                        false -> {
                            it.setCardBackgroundColor(backgroundColor)
                        }
                    }
                    updateAppViews(it)
                }

                is ViewGroup -> {
                    updateAppViews(it)
                }

                is RadioButton -> {
                    it.run {
                        setTextColor(config.textColor)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            buttonTintList =
                                ColorStateList(
                                    arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)),
                                    intArrayOf(
                                        config.textColor,
                                        config.textColor,
                                    ),
                                )
                        }
                    }
                }

                is CheckBox -> {
                    it.run {
                        setTextColor(config.textColor)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            buttonTintList =
                                ColorStateList(
                                    arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)),
                                    intArrayOf(
                                        config.textColor,
                                        config.textColor,
                                    ),
                                )
                        }
                    }
                }

                is SwitchCompat -> {
                    it.run {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && config.primaryColor == config.backgroundColor) {
                            trackTintList =
                                ColorStateList(
                                    arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)),
                                    intArrayOf(
                                        ColorUtils.setAlphaComponent(config.textColor, 190),
                                        config.textColor,
                                    ),
                                )
                            thumbTintList =
                                ColorStateList(
                                    arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)),
                                    intArrayOf(
                                        ColorUtils.setAlphaComponent(config.textColor, 255),
                                        config.textColor,
                                    ),
                                )
                        }
                    }
                }

                is ProgressBar -> {
                    if (Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.LOLLIPOP
                    ) {
                        it.indeterminateTintList = ColorStateList.valueOf(config.primaryColor)
                    }
                }
            }
        }
}

fun Context.updateCardViewPolicy(viewGroup: ViewGroup) {
    if (isNightMode()) return

    val cnt = viewGroup.childCount
    (0 until cnt)
        .map { viewGroup.getChildAt(it) }
        .forEach {
            when (it) {
                is FixedCardView -> {
                    if (it.fixedAppcompatPadding) {
                        it.useCompatPadding = true
                        it.cardElevation = dpToPixelFloatValue(2F)
                    } else {
                        it.useCompatPadding = false
                        it.cardElevation = 0F
                    }
                }

                is CardView -> {
                    var additionalMargin = 0

                    if (config.enableCardViewPolicy) {
                        if (it is ItemCardView && it.applyAdditionHorizontalMargin) {
                            additionalMargin = dpToPixel(3f)
                        }

                        it.useCompatPadding = true
                        it.cardElevation = dpToPixelFloatValue(2F)
                    } else {
                        it.useCompatPadding = false
                        it.cardElevation = 0F
                    }

                    val params = it.layoutParams as MarginLayoutParams
                    params.marginStart = additionalMargin
                    params.marginEnd = additionalMargin
                    it.layoutParams = params

                    updateCardViewPolicy(it)
                }

                is ViewGroup -> {
                    updateCardViewPolicy(it)
                }
            }
        }
}

fun Context.updateTextSize(
    viewGroup: ViewGroup,
    context: Context,
    addSize: Int,
) {
    if (isNightMode()) return

    val cnt = viewGroup.childCount
    val settingFontSize: Float = config.settingFontSize + addSize
    (0 until cnt)
        .map { index -> viewGroup.getChildAt(index) }
        .forEach {
            when (it) {
                is TextView -> {
                    it.setTextSize(TypedValue.COMPLEX_UNIT_PX, settingFontSize)
                }

                is ViewGroup -> {
                    updateTextSize(it, context, addSize)
                }
            }
        }
}

fun Context.initTextSize(viewGroup: ViewGroup) {
    if (isNightMode()) return

    val cnt = viewGroup.childCount
    val defaultFontSize: Float = dpToPixelFloatValue(SUPPORT_LANGUAGE_FONT_SIZE_DEFAULT_SP.toFloat())
    val settingFontSize: Float = config.settingFontSize
    (0 until cnt)
        .map { index -> viewGroup.getChildAt(index) }
        .forEach {
            when (it) {
                is FixedTextView -> {
                    if (it.applyGlobalSize) it.setTextSize(TypedValue.COMPLEX_UNIT_PX, settingFontSize)
                }

                is Button -> {}

                is TextView -> {
                    if (it.tag == "tabTitle") return
                    if (it.text == "Dashboard") return
                    if (it.id == R.id.locationLabel) {
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, settingFontSize * 0.7F)
                    } else {
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, settingFontSize)
                    }
                }

                is ViewGroup -> {
                    initTextSize(it)
                }
            }
        }
}

fun Context.initTextSize(textView: TextView) {
    if (isNightMode()) return

    val defaultFontSize: Float = dpToPixelFloatValue(SUPPORT_LANGUAGE_FONT_SIZE_DEFAULT_SP.toFloat())
    val settingFontSize: Float = config.settingFontSize
    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, settingFontSize)
}

fun Context.updateDrawableColorInnerCardView(
    resourceId: Int,
    color: Int = config.textColor,
) {
    if (isNightMode()) return
    changeDrawableIconColor(color, resourceId)
}

fun Context.updateDrawableColorInnerCardView(
    imageView: ImageView,
    color: Int = config.textColor,
) {
    if (isNightMode()) return
    changeDrawableIconColor(color, imageView)
}

fun Context.changeDrawableIconColor(
    color: Int,
    imageView: ImageView,
) {
    imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)
}

fun Context.changeDrawableIconColor(
    color: Int,
    resourceId: Int,
) {
    AppCompatResources.getDrawable(this, resourceId)?.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
        } else {
            setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}

@ColorInt
@SuppressLint("ResourceAsColor")
fun Context.getColorResCompat(
    @AttrRes id: Int,
): Int {
    val resolvedAttr = TypedValue()
    theme.resolveAttribute(id, resolvedAttr, true)
    val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
    return ContextCompat.getColor(this, colorRes)
}

fun Context.changeBitmapColor(
    drawableResourceId: Int,
    color: Int,
): Bitmap {
    val drawable = AppCompatResources.getDrawable(this, drawableResourceId)
    val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
        } else {
            setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
    }
    return bitmap
}

fun Context.getLabelBackground(): GradientDrawable {
    val strokeWidth = dpToPixel(1F)
    val strokeColor: Int = config.textColor
    return GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setStroke(strokeWidth, strokeColor)
    }
}
