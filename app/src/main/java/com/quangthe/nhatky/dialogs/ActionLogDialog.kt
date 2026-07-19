package com.quangthe.nhatky.dialogs

import android.app.Activity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.quangthe.nhatky.R
import com.quangthe.nhatky.models.ActionLog

class ActionLogDialog(val activity: Activity, private val actionLogs: List<ActionLog>, private val clearCallback: () -> Unit) {
    init {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_action_log, null)
        view.findViewById<TextView>(R.id.whats_new_content).text = getNewReleases()

        AlertDialog.Builder(activity)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton("Clear") { _, _ -> clearCallback.invoke() }
            .create().apply {
                activity.setupDialogStuff(view, this, R.string.app_name)
            }
    }

    private fun getNewReleases(): String {
        val sb = StringBuilder()

        actionLogs.forEach {
            sb.append("${it.className}-${it.signature}-${it.key}: ${it.value}\n")
            sb.append("\n")
        }

        return sb.toString()
    }
}
