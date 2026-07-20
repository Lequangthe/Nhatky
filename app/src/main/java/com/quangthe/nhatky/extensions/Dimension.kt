package com.quangthe.nhatky.extensions

import android.content.Context
import android.util.TypedValue
import com.quangthe.nhatky.enums.Calculation
import kotlin.math.ceil
import kotlin.math.roundToInt

fun Context.dpToPixelFloatValue(dp: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

fun Context.spToPixelFloatValue(sp: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)

fun Context.dpToPixel(
    dp: Float,
    policy: Calculation = Calculation.CEIL,
): Int {
    val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    return when (policy) {
        Calculation.CEIL -> ceil(px).toInt()
        Calculation.ROUND -> px.roundToInt()
        Calculation.FLOOR -> px.toInt()
    }
}

fun Context.dp(px: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, resources.displayMetrics).toInt()
