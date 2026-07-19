package com.quangthe.nhatky.views

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.quangthe.nhatky.R

open class ItemCardView : CardView {
    var applyAdditionHorizontalMargin: Boolean = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyAdditionHorizontalMargin = context.obtainStyledAttributes(attrs, R.styleable.ItemCardView).getBoolean(R.styleable.ItemCardView_applyAdditionHorizontalMargin, false)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}
