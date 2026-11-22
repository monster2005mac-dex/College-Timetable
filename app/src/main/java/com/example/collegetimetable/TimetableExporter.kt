package com.example.collegetimetable

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object TimetableExporter {

    fun exportToIcs(context: Context, entries: List<ClassEntry>) {
        val sb = StringBuilder()
        sb.append("BEGIN:VCALENDAR\n")
        sb.append("VERSION:2.0\n")
        sb.append("PRODID:-//College Timetable//NONSGML v1.0//EN\n")

        val today = LocalDate.now()

        entries.forEach { entry ->
            val dayOfWeek = try { DayOfWeek.of(entry.day) } catch (_: Exception) { DayOfWeek.MONDAY }
            val nextDate = today.with(TemporalAdjusters.nextOrSame(dayOfWeek))

            val startH = entry.startMin / 60
            val startM = entry.startMin % 60
            val endH = entry.endMin / 60
            val endM = entry.endMin % 60

            val dateStr = nextDate.toString().replace("-", "")
            val dtStart = "%sT%02d%02d00".format(dateStr, startH, startM)
            val dtEnd = "%sT%02d%02d00".format(dateStr, endH, endM)

            val rruleDay = when (entry.day) {
                1 -> "MO"
                2 -> "TU"
                3 -> "WE"
                4 -> "TH"
                5 -> "FR"
                6 -> "SA"
                else -> "SU"
            }

            sb.append("BEGIN:VEVENT\n")
            sb.append("SUMMARY:${entry.subject}\n")
            if (entry.room.isNotEmpty()) sb.append("LOCATION:${entry.room}\n")
            if (entry.teacher.isNotEmpty()) sb.append("DESCRIPTION:Instructor: ${entry.teacher}\n")
            sb.append("DTSTART:$dtStart\n")
            sb.append("DTEND:$dtEnd\n")
            sb.append("RRULE:FREQ=WEEKLY;BYDAY=$rruleDay\n")
            sb.append("END:VEVENT\n")
        }

        sb.append("END:VCALENDAR\n")

        try {
            val file = File(context.cacheDir, "College_Timetable.ics")
            file.writeText(sb.toString())

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/calendar"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share / Export Timetable (.ics)"))
        } catch (e: Exception) {
            // Share intent fallback
        }
    }

    fun shareAsJson(context: Context, entries: List<ClassEntry>) {
        val arr = JSONArray()
        entries.forEach { e ->
            arr.put(
                JSONObject()
                    .put("subject", e.subject)
                    .put("room", e.room)
                    .put("teacher", e.teacher)
                    .put("day", e.day)
                    .put("startMin", e.startMin)
                    .put("endMin", e.endMin)
                    .put("colorIndex", e.colorIndex)
            )
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, arr.toString(2))
        }
        context.startActivity(Intent.createChooser(intent, "Share Timetable Data"))
    }
}
