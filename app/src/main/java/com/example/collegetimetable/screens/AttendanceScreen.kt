package com.example.collegetimetable.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegetimetable.AttendanceRecord
import com.example.collegetimetable.BunkAdvice
import com.example.collegetimetable.ClassEntry
import com.example.collegetimetable.GlassCardShape
import com.example.collegetimetable.GlassErrorColor
import com.example.collegetimetable.GlassPanel
import com.example.collegetimetable.GlassPillShape
import com.example.collegetimetable.GlassSuccessColor
import com.example.collegetimetable.OnGlassErrorColor
import com.example.collegetimetable.OnGlassSuccessColor
import dev.chrisbanes.haze.HazeState

@Composable
fun AttendanceScreen(
    entries: List<ClassEntry>,
    attendanceRecords: Map<String, AttendanceRecord>,
    accentColor: Color,
    hazeState: HazeState,
    onAttendanceUpdated: (String, AttendanceRecord) -> Unit
) {
    var targetPct by remember { mutableFloatStateOf(75f) }

    // Derive unique subject list from entries + attendance records
    val subjects = remember(entries, attendanceRecords) {
        (entries.map { it.subject } + attendanceRecords.keys).distinct().filter { it.isNotEmpty() }
    }

    val totalAttended = attendanceRecords.values.sumOf { it.attended }
    val totalConducted = attendanceRecords.values.sumOf { it.totalConducted }
    val overallPct = if (totalConducted == 0) 100f else (totalAttended.toFloat() / totalConducted.toFloat()) * 100f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overall Attendance Hero Banner
        item(key = "overall_banner") {
            GlassPanel(
                hazeState = hazeState,
                modifier = Modifier.fillMaxWidth(),
                shape = GlassCardShape,
                shadowElevation = 12.dp,
                glassColor = accentColor,
                glassAlphaTop = 0.28f,
                glassAlphaBottom = 0.12f
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.3f))
                                    .border(1.dp, accentColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Percent,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Attendance & Bunk Tracker 🎓",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Target: ${targetPct.toInt()}% minimum",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Text(
                            text = "%.1f%%".format(overallPct),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (overallPct >= targetPct) GlassSuccessColor else GlassErrorColor
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Threshold Selector Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target %:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        listOf(75f, 80f, 85f).forEach { pct ->
                            val isSelected = targetPct == pct
                            Box(
                                modifier = Modifier
                                    .clip(GlassPillShape)
                                    .background(if (isSelected) accentColor else Color.White.copy(alpha = 0.12f))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                        GlassPillShape
                                    )
                                    .clickable { targetPct = pct }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${pct.toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subject Attendance Cards
        if (subjects.isEmpty()) {
            item(key = "empty_attendance") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add class slots to track subject attendance!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            items(subjects, key = { it }) { subject ->
                val record = attendanceRecords[subject] ?: AttendanceRecord(subject = subject)
                val advice = record.getBunkAdvice(targetPct)
                val animatedPct by animateFloatAsState(targetValue = record.percentage / 100f, label = "att_progress")

                GlassPanel(
                    hazeState = hazeState,
                    modifier = Modifier.fillMaxWidth(),
                    shape = GlassCardShape,
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subject,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${record.attended} / ${record.totalConducted} attended",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }

                            Text(
                                text = "%.1f%%".format(record.percentage),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (record.percentage >= targetPct) GlassSuccessColor else GlassErrorColor
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedPct)
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(if (record.percentage >= targetPct) accentColor else GlassErrorColor)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bunk Advice Chip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (advice) {
                                is BunkAdvice.SafeToSkip -> {
                                    Box(
                                        modifier = Modifier
                                            .clip(GlassPillShape)
                                            .background(GlassSuccessColor.copy(alpha = 0.25f))
                                            .border(1.dp, GlassSuccessColor, GlassPillShape)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (advice.skipCount > 0) "Safe to skip ${advice.skipCount} class(es) 🟢" else "On track 🎯",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = OnGlassSuccessColor
                                        )
                                    }
                                }

                                is BunkAdvice.NeedToAttend -> {
                                    Box(
                                        modifier = Modifier
                                            .clip(GlassPillShape)
                                            .background(GlassErrorColor.copy(alpha = 0.25f))
                                            .border(1.dp, GlassErrorColor, GlassPillShape)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Attend ${advice.attendCount} consecutive class(es) 🔴",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = OnGlassErrorColor
                                        )
                                    }
                                }

                                BunkAdvice.NoData -> {
                                    Text(
                                        text = "No classes recorded yet",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            // Action Buttons (+ Attended / + Missed)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(GlassPillShape)
                                        .background(GlassSuccessColor.copy(alpha = 0.3f))
                                        .clickable {
                                            onAttendanceUpdated(
                                                subject,
                                                record.copy(attended = record.attended + 1)
                                            )
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("+ Present", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(GlassPillShape)
                                        .background(GlassErrorColor.copy(alpha = 0.3f))
                                        .clickable {
                                            onAttendanceUpdated(
                                                subject,
                                                record.copy(missed = record.missed + 1)
                                            )
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("+ Bunked", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
