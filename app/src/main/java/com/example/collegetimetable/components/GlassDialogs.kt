package com.example.collegetimetable.components

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.collegetimetable.ClassEntry
import com.example.collegetimetable.GlassDialogShape
import com.example.collegetimetable.GlassErrorColor
import com.example.collegetimetable.GlassInputShape
import com.example.collegetimetable.GlassPanel
import com.example.collegetimetable.GlassPillShape
import com.example.collegetimetable.OnGlassErrorColor
import com.example.collegetimetable.classColors
import com.example.collegetimetable.dayShort
import com.example.collegetimetable.formatTime
import dev.chrisbanes.haze.HazeState

enum class EventCategory(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val titleLabel: String,
    val colorIdx: Int
) {
    LECTURE("Lecture 📚", Icons.Default.Class, "Subject Name", 0),
    LAB("Lab 🔬", Icons.Default.Class, "Lab Name", 1),
    STUDY("Study Session 📖", Icons.Default.MenuBook, "Topic / Subject", 2),
    TASK("Personal Task ⚡", Icons.Default.TaskAlt, "Task Name", 3),
    GYM("Fitness 🏋️", Icons.Default.FitnessCenter, "Workout / Activity", 4),
    MEETING("Club / Meeting 👥", Icons.Default.Groups, "Meeting Title", 5)
}

@Composable
fun GlassClassDialog(
    entry: ClassEntry,
    isNew: Boolean,
    findConflict: (ClassEntry) -> ClassEntry?,
    accentColor: Color,
    hazeState: HazeState,
    is24Hour: Boolean = false,
    onSaveEntries: (List<ClassEntry>) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var selectedCategory by remember { mutableStateOf(EventCategory.LECTURE) }
    var subject by remember { mutableStateOf(entry.subject) }
    var room by remember { mutableStateOf(entry.room) }
    var teacher by remember { mutableStateOf(entry.teacher) }

    // Multi-Day / Repeat selection (set of days e.g. 1..7)
    var selectedDays by remember { mutableStateOf(setOf(entry.day)) }

    var start by remember { mutableIntStateOf(entry.startMin) }
    var end by remember { mutableIntStateOf(entry.endMin) }
    var color by remember { mutableIntStateOf(entry.colorIndex) }
    var error by remember { mutableStateOf<String?>(null) }

    fun pickTime(initial: Int, onPicked: (Int) -> Unit) {
        TimePickerDialog(
            context,
            { _, h, m -> onPicked(h * 60 + m) },
            initial / 60, initial % 60, is24Hour
        ).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassPanel(
            hazeState = hazeState,
            modifier = Modifier.fillMaxWidth(),
            shape = GlassDialogShape,
            shadowElevation = 24.dp,
            glassAlphaTop = 0.35f,
            glassAlphaBottom = 0.20f
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.3f))
                            .border(1.dp, accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = selectedCategory.icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isNew) "Add Event / Task" else "Edit Slot",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Event Category Presets
                Text(
                    text = "Event Category",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EventCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(if (isSelected) accentColor else Color.White.copy(alpha = 0.1f))
                                .border(
                                    1.dp,
                                    if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                    GlassPillShape
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedCategory = category
                                    color = category.colorIdx
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )
                        }
                    }
                }

                // Title Input
                GlassInputField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = selectedCategory.titleLabel,
                    icon = selectedCategory.icon
                )

                // Room / Location Input (Optional)
                GlassInputField(
                    value = room,
                    onValueChange = { room = it },
                    label = "Room / Location (optional)",
                    icon = Icons.Default.LocationOn
                )

                // Teacher / Instructor Input (Optional for Lectures & Labs)
                if (selectedCategory == EventCategory.LECTURE || selectedCategory == EventCategory.LAB) {
                    GlassInputField(
                        value = teacher,
                        onValueChange = { teacher = it },
                        label = "Teacher / Instructor (optional)",
                        icon = Icons.Default.Person
                    )
                }

                // Days Selection & Repeat Daily
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedule Day(s)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    // "Repeat Everyday" Toggle Chip
                    val isEveryday = selectedDays.size == 7
                    Box(
                        modifier = Modifier
                            .clip(GlassPillShape)
                            .background(if (isEveryday) accentColor else Color.White.copy(alpha = 0.12f))
                            .border(1.dp, if (isEveryday) Color.White else Color.White.copy(alpha = 0.25f), GlassPillShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedDays = if (isEveryday) setOf(entry.day) else (1..7).toSet()
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Repeat Everyday ⚡",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dayShort.forEachIndexed { i, label ->
                        val dayNum = i + 1
                        val isSelected = selectedDays.contains(dayNum)

                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(if (isSelected) accentColor else Color.White.copy(alpha = 0.1f))
                                .border(
                                    1.dp,
                                    if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                    GlassPillShape
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedDays = if (isSelected) {
                                        if (selectedDays.size > 1) selectedDays - dayNum else selectedDays
                                    } else {
                                        selectedDays + dayNum
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )
                        }
                    }
                }

                // Time Pickers
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GlassTimePickerButton(
                        label = "From ${formatTime(start, is24Hour)}",
                        modifier = Modifier.weight(1f),
                        onClick = { pickTime(start) { start = it } }
                    )
                    GlassTimePickerButton(
                        label = "To ${formatTime(end, is24Hour)}",
                        modifier = Modifier.weight(1f),
                        onClick = { pickTime(end) { end = it } }
                    )
                }

                // Color Selection Circles
                Text(
                    text = "Event Accent Color",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    classColors.forEachIndexed { i, c ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(
                                    width = if (color == i) 3.dp else 1.dp,
                                    color = if (color == i) Color.White else Color.White.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .clickable { color = i }
                        )
                    }
                }

                // Error Banner
                error?.let { errText ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassErrorColor.copy(alpha = 0.25f))
                            .border(1.dp, GlassErrorColor, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = errText,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnGlassErrorColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isNew) {
                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(GlassErrorColor.copy(alpha = 0.2f))
                                .border(1.dp, GlassErrorColor.copy(alpha = 0.5f), GlassPillShape)
                                .clickable(onClick = onDelete)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = OnGlassErrorColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Delete",
                                    color = OnGlassErrorColor,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
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
                                    val trimmedSubject = subject.trim()
                                    if (trimmedSubject.isEmpty()) {
                                        error = "Enter a title/subject name"
                                    } else if (end <= start) {
                                        error = "End time must be after start time"
                                    } else {
                                        val candidateEntries = selectedDays.map { dayVal ->
                                            entry.copy(
                                                id = if (isNew || dayVal != entry.day) java.util.UUID.randomUUID().toString() else entry.id,
                                                subject = trimmedSubject,
                                                room = room.trim(),
                                                teacher = teacher.trim(),
                                                day = dayVal,
                                                startMin = start,
                                                endMin = end,
                                                colorIndex = color
                                            )
                                        }

                                        var clash: ClassEntry? = null
                                        for (cand in candidateEntries) {
                                            clash = findConflict(cand)
                                            if (clash != null) break
                                        }

                                        if (clash != null) {
                                            error = "Overlaps with ${clash.subject} " +
                                                    "(${formatTime(clash.startMin, is24Hour)} – ${formatTime(clash.endMin, is24Hour)})"
                                        } else {
                                            onSaveEntries(candidateEntries)
                                        }
                                    }
                                }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Save",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.7f)) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedContainerColor = Color.White.copy(alpha = 0.08f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = GlassInputShape,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun GlassTimePickerButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(GlassInputShape)
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.3f), GlassInputShape)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
