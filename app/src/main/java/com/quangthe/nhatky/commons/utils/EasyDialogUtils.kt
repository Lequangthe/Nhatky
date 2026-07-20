package com.quangthe.nhatky.commons.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import com.quangthe.nhatky.R
import com.quangthe.nhatky.adapters.SecondItemAdapter
import com.quangthe.nhatky.extensions.dpToPixel

fun createSecondsPickerBuilder(
    context: Context,
    itemClickListener: AdapterView.OnItemClickListener,
    second: Int,
): AlertDialog.Builder {
    val builder = AlertDialog.Builder(context)
    builder.setNegativeButton(context.getString(android.R.string.cancel), null)
    builder.setTitle(context.getString(R.string.common_create_seconds))
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val secondsPicker = inflater.inflate(R.layout.dialog_seconds_picker, null)
    val listView = secondsPicker.findViewById<ListView>(R.id.seconds)
    val listSecond = ArrayList<Map<String, String>>()
    for (i in 0..59) {
        val map = hashMapOf<String, String>()
        map["label"] = i.toString() + "s"
        map["value"] = i.toString()
        listSecond.add(map)
    }
    val adapter = SecondItemAdapter(context, R.layout.item_second, listSecond, second)
    listView.adapter = adapter
    listView.onItemClickListener = itemClickListener
    builder.setView(secondsPicker)
    return builder
}

fun openCustomOptionMenu(
    content: View,
    parent: View,
): PopupWindow {
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val popup: PopupWindow =
        PopupWindow(content, width, height, true).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            showAtLocation(
                parent,
                Gravity.TOP or Gravity.RIGHT,
                0,
                parent.context.dpToPixel(24F),
            )
        }
    content.x = 1000f
    content.y = 0f
    val animX = ObjectAnimator.ofFloat(content, "x", 0f)
    val animY = ObjectAnimator.ofFloat(content, "y", 0f)
    AnimatorSet().apply {
        playTogether(animX, animY)
        duration = 390
        start()
    }
    return popup
}
