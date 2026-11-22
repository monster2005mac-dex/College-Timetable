package com.example.collegetimetable

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

enum class ExamType(val label: String) {
    EXAM("Midterm / Final Exam"),
    QUIZ("Quiz / Test"),
    ASSIGNMENT("Homework / Project")
}

enum class ExamPriority(val label: String) {
    HIGH("High Priority 🔴"),
    MEDIUM("Medium Priority 🟡"),
    LOW("Low Priority 🟢")
}

data class ExamEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val dateIso: String, // YYYY-MM-DD
    val timeMin: Int = 9 * 60,
    val type: ExamType = ExamType.EXAM,
    val priority: ExamPriority = ExamPriority.HIGH,
    val notes: String = ""
) {
    val date: LocalDate get() = try { LocalDate.parse(dateIso) } catch (_: Exception) { LocalDate.now() }

    fun daysUntil(currentDate: LocalDate = LocalDate.now()): Long {
        return ChronoUnit.DAYS.between(currentDate, date)
    }

    fun getFormattedCountdown(currentDate: LocalDate = LocalDate.now()): String {
        val days = daysUntil(currentDate)
        return when {
            days < 0 -> "Overdue / Passed"
            days == 0L -> "Due Today ⚡"
            days == 1L -> "Due Tomorrow ⚠️"
            else -> "In $days days"
        }
    }
}

class ExamRepository(context: Context) {
    private val prefs = context.getSharedPreferences("exams_prefs", Context.MODE_PRIVATE)

    fun loadAll(): List<ExamEntry> {
        val raw = prefs.getString("exams", null) ?: return emptyList()
        return try {
            val list = mutableListOf<ExamEntry>()
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    ExamEntry(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        subject = obj.optString("subject"),
                        dateIso = obj.getString("dateIso"),
                        timeMin = obj.optInt("timeMin", 9 * 60),
                        type = try { ExamType.valueOf(obj.optString("type", "EXAM")) } catch (_: Exception) { ExamType.EXAM },
                        priority = try { ExamPriority.valueOf(obj.optString("priority", "HIGH")) } catch (_: Exception) { ExamPriority.HIGH },
                        notes = obj.optString("notes")
                    )
                )
            }
            list.sortedBy { it.dateIso }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveAll(list: List<ExamEntry>) {
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(
                JSONObject()
                    .put("id", item.id)
                    .put("title", item.title)
                    .put("subject", item.subject)
                    .put("dateIso", item.dateIso)
                    .put("timeMin", item.timeMin)
                    .put("type", item.type.name)
                    .put("priority", item.priority.name)
                    .put("notes", item.notes)
            )
        }
        prefs.edit().putString("exams", arr.toString()).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
