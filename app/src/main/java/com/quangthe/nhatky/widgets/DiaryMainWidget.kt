package com.quangthe.nhatky.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.quangthe.nhatky.R
import com.quangthe.nhatky.services.DiaryMainWidgetService

class DiaryMainWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val intent = Intent(context, DiaryMainWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            val views = RemoteViews(context.packageName, R.layout.widget_diary_main).apply {
                setRemoteAdapter(R.id.diaryListView, intent)
                setEmptyView(R.id.diaryListView, R.id.widget_event_list_empty)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
