package com.example.collegetimetable

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.ceil

/** Attendance record for a single subject */
data class AttendanceRecord(
    val subject: String,
    val attended: Int = 0,
    val missed: Int = 0,
    val cancelled: Int = 0,
    val credits: Int = 3,
    val expectedGradePoints: Double = 4.0 // 4.0 = A, 3.0 = B, etc.
) {
    val totalConducted: Int get() = attended + missed

    val percentage: Float
        get() = if (totalConducted == 0) 100f else (attended.toFloat() / totalConducted.toFloat()) * 100f

    /** Calculates how many classes can be safely skipped or must be attended to reach target % (e.g. 75%) */
    fun getBunkAdvice(targetPercentage: Float = 75f): BunkAdvice {
        if (totalConducted == 0) return BunkAdvice.NoData

        val currentPct = percentage
        val targetFraction = targetPercentage / 100f

        return if (currentPct >= targetPercentage) {
            val maxTotal = (attended / targetFraction).toDouble()
            val safeSkips = (maxTotal - totalConducted).toInt().coerceAtLeast(0)
            BunkAdvice.SafeToSkip(safeSkips)
        } else {
            val num = (targetFraction * totalConducted) - attended
            val den = 1f - targetFraction
            val needed = ceil((num / den).toDouble()).toInt().coerceAtLeast(1)
            BunkAdvice.NeedToAttend(needed)
        }
    }
}

sealed class BunkAdvice {
    data class SafeToSkip(val skipCount: Int) : BunkAdvice()
    data class NeedToAttend(val attendCount: Int) : BunkAdvice()
    object NoData : BunkAdvice()
}

/** Manages saving and loading attendance records */
class AttendanceRepository(context: Context) {
    private val prefs = context.getSharedPreferences("attendance_prefs", Context.MODE_PRIVATE)

    fun loadAll(): Map<String, AttendanceRecord> {
        val raw = prefs.getString("records", null) ?: return emptyMap()
        return try {
            val map = mutableMapOf<String, AttendanceRecord>()
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val subject = obj.getString("subject")
                map[subject] = AttendanceRecord(
                    subject = subject,
                    attended = obj.optInt("attended", 0),
                    missed = obj.optInt("missed", 0),
                    cancelled = obj.optInt("cancelled", 0),
                    credits = obj.optInt("credits", 3),
                    expectedGradePoints = obj.optDouble("expectedGradePoints", 4.0)
                )
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun saveAll(records: Map<String, AttendanceRecord>) {
        val arr = JSONArray()
        records.values.forEach { r ->
            arr.put(
                JSONObject()
                    .put("subject", r.subject)
                    .put("attended", r.attended)
                    .put("missed", r.missed)
                    .put("cancelled", r.cancelled)
                    .put("credits", r.credits)
                    .put("expectedGradePoints", r.expectedGradePoints)
            )
        }
        prefs.edit().putString("records", arr.toString()).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
