package com.quangthe.nhatky.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.quangthe.nhatky.R

class FixedTextView : AppCompatTextView {
    var applyHighLight: Boolean = false
    var applyBoldStyle: Boolean = false
    var applyGlobalColor: Boolean = true
    var applyGlobalSize: Boolean = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FixedTextView)
        applyHighLight = typedArray.getBoolean(R.styleable.FixedTextView_applyHighLight, false)
        applyBoldStyle = typedArray.getBoolean(R.styleable.FixedTextView_applyBoldStyle, false)
        applyGlobalColor = typedArray.getBoolean(R.styleable.FixedTextView_applyGlobalColor, true)
        applyGlobalSize = typedArray.getBoolean(R.styleable.FixedTextView_applyGlobalSize, true)
        typedArray.recycle()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setColors(textColor: Int, accentColor: Int, backgroundColor: Int) {
        setTextColor(textColor)
    }
}
