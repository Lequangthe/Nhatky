package com.quangthe.nhatky.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.config

open class FixedCardView : CardView {
    var fixedAppcompatPadding: Boolean = false
    var applyCardBackgroundColor: Boolean = false
    var dashboardInnerCard: Boolean = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        fixedAppcompatPadding =
            context
                .obtainStyledAttributes(
                    attrs,
                    R.styleable.FixedCardView,
                ).getBoolean(R.styleable.FixedCardView_fixedAppcompatPadding, false)
        applyCardBackgroundColor =
            context
                .obtainStyledAttributes(
                    attrs,
                    R.styleable.FixedCardView,
                ).getBoolean(R.styleable.FixedCardView_applyCardBackgroundColor, false)
        dashboardInnerCard =
            context.obtainStyledAttributes(attrs, R.styleable.FixedCardView).getBoolean(R.styleable.FixedCardView_dashboardInnerCard, false)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (fixedAppcompatPadding) useCompatPadding = true
    }
}
