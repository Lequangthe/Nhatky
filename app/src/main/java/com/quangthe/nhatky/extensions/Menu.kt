package com.quangthe.nhatky.extensions

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.view.MenuItem
import com.quangthe.nhatky.commons.utils.FontUtils

fun Context.applyFontToMenuItem(mi: MenuItem) {
    val mNewTitle = SpannableString(mi.title)
    mNewTitle.setSpan(CustomTypefaceSpan("", FontUtils.getCommonTypeface(this)!!), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    mi.title = mNewTitle
}
