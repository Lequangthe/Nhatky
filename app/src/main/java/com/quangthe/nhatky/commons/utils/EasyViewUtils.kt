package com.quangthe.nhatky.commons.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.quangthe.nhatky.core.config.ColorConstants
import com.quangthe.nhatky.extensions.config

fun boldString(
    context: Context,
    textView: TextView?,
) {
    if (context.config.boldStyleEnable) {
        boldStringForce(textView)
    }
}

fun boldStringForce(textView: TextView?) {
    textView?.let { tv ->
        val spannableString = SpannableString(tv.text)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, tv.text.length, 0)
        tv.text = spannableString
    }
}

fun warningString(textView: TextView) {
    val spannableString = SpannableString(textView.text)
    spannableString.setSpan(
        UnderlineSpan(),
        0,
        textView.text.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    spannableString.setSpan(
        StyleSpan(Typeface.ITALIC),
        0,
        textView.text.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    textView.text = spannableString
}

fun highlightString(textView: TextView) {
    val spannableString = SpannableString(textView.text)
    spannableString.setSpan(
        BackgroundColorSpan(ColorConstants.HIGHLIGHT_COLOR),
        0,
        textView.text.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    spannableString.setSpan(
        ForegroundColorSpan(Color.BLACK),
        0,
        textView.text.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
    )
    textView.text = spannableString
}

fun highlightStringIgnoreCase(
    textView: TextView?,
    input: String?,
    highlightColor: Int = ColorConstants.HIGHLIGHT_COLOR,
) {
    textView?.let { tv ->
        input?.let { targetString ->
            val inputLower = targetString.lowercase()
            val contentsLower = tv.text.toString().lowercase()
            val spannableString = SpannableString(tv.text)
            removeSpans(spannableString)

            var indexOfKeyword = contentsLower.indexOf(inputLower)
            while (indexOfKeyword >= 0) {
                spannableString.setSpan(
                    BackgroundColorSpan(highlightColor),
                    indexOfKeyword,
                    indexOfKeyword + inputLower.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
                spannableString.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    indexOfKeyword,
                    indexOfKeyword + targetString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )

                indexOfKeyword =
                    contentsLower.indexOf(inputLower, indexOfKeyword + inputLower.length)
            }
            tv.text = spannableString
        }
    }
}

fun highlightString(
    textView: TextView?,
    input: String?,
    highlightColor: Int = ColorConstants.HIGHLIGHT_COLOR,
) {
    textView?.let { tv ->
        input?.let { targetString ->
            val spannableString = SpannableString(tv.text)
            removeSpans(spannableString)

            var indexOfKeyword = spannableString.toString().indexOf(targetString)
            while (indexOfKeyword >= 0) {
                spannableString.setSpan(
                    BackgroundColorSpan(highlightColor),
                    indexOfKeyword,
                    indexOfKeyword + targetString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
                spannableString.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    indexOfKeyword,
                    indexOfKeyword + targetString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )

                indexOfKeyword =
                    spannableString
                        .toString()
                        .indexOf(targetString, indexOfKeyword + targetString.length)
            }

            tv.text = spannableString
        }
    }
}

fun removeSpans(spannableString: SpannableString) {
    spannableString
        .getSpans(0, spannableString.length, BackgroundColorSpan::class.java)
        ?.forEach { spannableString.removeSpan(it) }
    spannableString
        .getSpans(0, spannableString.length, ForegroundColorSpan::class.java)
        ?.forEach { spannableString.removeSpan(it) }
}

@SuppressLint("ClickableViewAccessibility")
fun disableTouchEvent(view: View) {
    view.setOnTouchListener { _, _ -> true }
}

fun applyMarkDownEllipsize(
    textContents: TextView,
    sequence: Int,
    delayMillis: Long = 0,
) {
    Handler(Looper.getMainLooper()).postDelayed({
        if (textContents.tag == sequence) {
            val max = textContents.maxLines
            val layout = textContents.layout
            if ((layout?.lineCount ?: 0) > max) {
                val end = layout.getLineEnd(max - 1)
                textContents.setText(
                    textContents.text.subSequence(0, end - 1),
                    TextView.BufferType.SPANNABLE,
                )
                textContents.append("…")
            }
        }
    }, delayMillis)
}
