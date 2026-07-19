package com.quangthe.nhatky.services

import android.content.Intent
import android.widget.RemoteViewsService
import com.quangthe.nhatky.widgets.DiaryMainWidgetFactory

class DiaryMainWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return DiaryMainWidgetFactory(this.applicationContext)
    }
}
