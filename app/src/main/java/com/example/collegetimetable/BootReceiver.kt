package com.example.collegetimetable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repo = TimetableRepository(context)
            val entries = repo.load()
            AlarmScheduler.rescheduleAll(context, entries)
        }
    }
}
