package com.quangthe.nhatky.widgets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.quangthe.nhatky.R
import com.quangthe.nhatky.helper.DIARY_SEQUENCE
import com.quangthe.nhatky.repositories.DiaryRepository
import com.quangthe.nhatky.models.Diary

class DiaryMainWidgetFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var diaryList: List<Diary> = ArrayList()
    private val diaryRepository = DiaryRepository()

    override fun onCreate() {}
    override fun onDataSetChanged() {
        // Implementation for data loading
    }
    override fun onDestroy() {}
    override fun getCount(): Int = diaryList.size
    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_item_diary_main)
        val diary = diaryList[position]
        views.setTextViewText(R.id.text1, diary.title)
        
        val fillInIntent = Intent().apply {
            val extras = Bundle()
            extras.putInt(DIARY_SEQUENCE, diary.sequence)
            putExtras(extras)
        }
        views.setOnClickFillInIntent(R.id.widgetItem, fillInIntent)
        return views
    }
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
}
