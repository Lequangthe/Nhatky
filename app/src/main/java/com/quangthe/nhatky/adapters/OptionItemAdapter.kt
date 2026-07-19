package com.quangthe.nhatky.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.quangthe.nhatky.commons.utils.FontUtils
import com.quangthe.nhatky.R
import com.quangthe.nhatky.extensions.changeDrawableIconColor
import com.quangthe.nhatky.extensions.config
import com.quangthe.nhatky.extensions.initTextSize
import com.quangthe.nhatky.extensions.updateTextColors

/**
 * Refactored code on 2019-12-25.
 *
 */
class OptionItemAdapter(
        val activity: Activity,
        private val layoutResourceId: Int,
        private val list: List<Map<String, String>>,
        private val selectedValueFloat: Float?,
        private val selectedValueString: String? = null,
        private val visibleCheckIcon: Boolean = true
) : ArrayAdapter<Map<String, String>>(activity , layoutResourceId, list) {
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView: View = convertView ?: LayoutInflater.from(parent.context).inflate(this.layoutResourceId, parent, false)
        if (itemView is ViewGroup) {
            activity.run {
                initTextSize(itemView)
                updateTextColors(itemView)
                FontUtils.setFontsTypeface(this, null, itemView)
            }
        }

        when (itemView.tag is ViewHolder) {
            true -> itemView.tag as ViewHolder
            false -> {
                val viewHolder = ViewHolder(itemView.findViewById(R.id.textView), itemView.findViewById(R.id.checkIcon))
                itemView.tag = viewHolder
                viewHolder
            }
        }.run {
            imageView.visibility = if (visibleCheckIcon) View.VISIBLE else View.GONE
            activity.changeDrawableIconColor(context.config.textColor, imageView)
            val optionValue = list[position]["optionValue"] ?: "0"
            if ((selectedValueFloat != null && selectedValueFloat == optionValue.toFloat()) || selectedValueString == optionValue) {
//                activity.updateDrawableColorInnerCardView(R.drawable.ic_check_mark)
//                ContextCompat.getDrawable(context, R.drawable.ic_check_mark).run {
//                    imageView.setImageDrawable(this)
//                    imageView.alpha = 1F
//                }
                imageView.alpha = 1F
            } else {
//                imageView.setImageBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.ic_check_mark_off))
                imageView.alpha = 0.2F
            }
            textView.text = list[position]["optionTitle"]
        }


        return itemView
    }

    private class ViewHolder(val textView: TextView, val imageView: ImageView)
}
