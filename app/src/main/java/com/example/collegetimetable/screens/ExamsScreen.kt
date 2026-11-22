package com.example.collegetimetable.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.collegetimetable.ClassEntry
import com.example.collegetimetable.ExamEntry
import com.example.collegetimetable.ExamPriority
import com.example.collegetimetable.GlassCardShape
import com.example.collegetimetable.GlassDialogShape
import com.example.collegetimetable.GlassErrorColor
import com.example.collegetimetable.GlassPanel
import com.example.collegetimetable.GlassPillShape
import com.example.collegetimetable.GlassSuccessColor
import com.example.collegetimetable.GlassWarningColor
import com.example.collegetimetable.OnGlassErrorColor
import com.example.collegetimetable.components.GlassInputField
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate

@Composable
fun ExamsScreen(
    exams: List<ExamEntry>,
    entries: List<ClassEntry>,
    accentColor: Color,
    hazeState: HazeState,
    onSaveExam: (ExamEntry) -> Unit,
    onDeleteExam: (String) -> Unit
) {
    var dialogExam by remember { mutableStateOf<ExamEntry?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
            item(key = "exams_banner") {
                GlassPanel(
                    hazeState = hazeState,
                    modifier = Modifier.fillMaxWidth(),
                    shape = GlassCardShape,
                    shadowElevation = 10.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
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
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Exams & Assignments 📝",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${exams.size} Upcoming Deadlines",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(accentColor)
                                .clickable {
                                    dialogExam = ExamEntry(
                                        title = "",
                                        subject = entries.firstOrNull()?.subject ?: "",
                                        dateIso = LocalDate.now().plusDays(3).toString()
                                    )
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Exam",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Exams List
            if (exams.isEmpty()) {
                item(key = "empty_exams") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No upcoming exams or assignments! Tap + Add to record one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(exams, key = { it.id }) { exam ->
                    GlassPanel(
                        hazeState = hazeState,
                        modifier = Modifier.fillMaxWidth(),
                        shape = GlassCardShape,
                        shadowElevation = 8.dp,
                        onClick = { dialogExam = exam }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Priority Color Bar
                            Box(
                                modifier = Modifier
                                    .size(width = 5.dp, height = 44.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        when (exam.priority) {
                                            ExamPriority.HIGH -> GlassErrorColor
                                            ExamPriority.MEDIUM -> GlassWarningColor
                                            ExamPriority.LOW -> GlassSuccessColor
                                        }
                                    )
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = exam.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Countdown Tag
                                    Box(
                                        modifier = Modifier
                                            .clip(GlassPillShape)
                                            .background(accentColor.copy(alpha = 0.25f))
                                            .border(1.dp, accentColor, GlassPillShape)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = exam.getFormattedCountdown(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "${exam.type.label} • ${exam.subject}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )

                                Text(
                                    text = "Date: ${exam.dateIso}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Exam Glass Dialog
    dialogExam?.let { exam ->
        GlassExamDialog(
            exam = exam,
            hazeState = hazeState,
            accentColor = accentColor,
            onSave = { onSaveExam(it); dialogExam = null },
            onDelete = { onDeleteExam(exam.id); dialogExam = null },
            onDismiss = { dialogExam = null }
        )
    }
}

@Composable
fun GlassExamDialog(
    exam: ExamEntry,
    hazeState: HazeState,
    accentColor: Color,
    onSave: (ExamEntry) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(exam.title) }
    var subject by remember { mutableStateOf(exam.subject) }
    var dateIso by remember { mutableStateOf(exam.dateIso) }
    var priority by remember { mutableStateOf(exam.priority) }

    fun pickDate() {
        val curr = try { LocalDate.parse(dateIso) } catch (_: Exception) { LocalDate.now() }
        DatePickerDialog(
            context,
            { _, y, m, d ->
                dateIso = "%04d-%02d-%02d".format(y, m + 1, d)
            },
            curr.year, curr.monthValue - 1, curr.dayOfMonth
        ).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassPanel(
            hazeState = hazeState,
            modifier = Modifier.fillMaxWidth(),
            shape = GlassDialogShape,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (exam.title.isEmpty()) "Add Deadline" else "Edit Deadline",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                GlassInputField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title (e.g. Midterm 1)",
                    icon = Icons.Default.Assignment
                )

                GlassInputField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = "Subject Name",
                    icon = Icons.Default.Assignment
                )

                // Date Picker Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .clickable { pickDate() }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Due Date: $dateIso",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Priority Selector
                Text(
                    text = "Priority Level",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExamPriority.entries.forEach { p ->
                        val isSelected = priority == p
                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(if (isSelected) accentColor else Color.White.copy(alpha = 0.1f))
                                .border(
                                    1.dp,
                                    if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                    GlassPillShape
                                )
                                .clickable { priority = p }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = p.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (exam.title.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(GlassErrorColor.copy(alpha = 0.2f))
                                .border(1.dp, GlassErrorColor.copy(alpha = 0.4f), GlassPillShape)
                                .clickable(onClick = onDelete)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = OnGlassErrorColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", color = OnGlassErrorColor, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                        }
                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(accentColor)
                                .clickable {
                                    if (title.isNotEmpty()) {
                                        onSave(exam.copy(title = title.trim(), subject = subject.trim(), dateIso = dateIso, priority = priority))
                                    }
                                }
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Text("Save", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
