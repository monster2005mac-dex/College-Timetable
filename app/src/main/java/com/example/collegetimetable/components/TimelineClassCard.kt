package com.example.collegetimetable.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegetimetable.ClassEntry
import com.example.collegetimetable.ClassProgressStatus
import com.example.collegetimetable.GlassCardShape
import com.example.collegetimetable.GlassErrorColor
import com.example.collegetimetable.GlassPanel
import com.example.collegetimetable.GlassPillShape
import com.example.collegetimetable.GlassSuccessColor
import com.example.collegetimetable.OnGlassErrorColor
import com.example.collegetimetable.OnGlassSuccessColor
import com.example.collegetimetable.classColors
import com.example.collegetimetable.formatTime
import dev.chrisbanes.haze.HazeState

@Composable
fun TimelineClassCard(
    entry: ClassEntry,
    progressStatus: ClassProgressStatus,
    hazeState: HazeState,
    is24Hour: Boolean = false,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val accent = classColors[entry.colorIndex % classColors.size]
    var isExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ongoing_pulse"
    )

    val cardAlpha = when (progressStatus) {
        is ClassProgressStatus.Finished -> 0.60f
        else -> 1.0f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left Timeline Column (Time Indicator + Axis Node)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(68.dp)
        ) {
            Text(
                text = formatTime(entry.startMin, is24Hour),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Timeline Node Circle
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when (progressStatus) {
                            is ClassProgressStatus.Ongoing -> accent
                            is ClassProgressStatus.Finished -> GlassSuccessColor
                            else -> Color.White.copy(alpha = 0.4f)
                        }
                    )
                    .border(
                        2.dp,
                        if (progressStatus is ClassProgressStatus.Ongoing) Color.White else Color.Transparent,
                        CircleShape
                    )
            )

            // Timeline Vertical Line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(64.dp)
                    .background(
                        if (progressStatus is ClassProgressStatus.Ongoing) {
                            Brush.verticalGradient(listOf(accent, Color.White.copy(alpha = 0.2f)))
                        } else {
                            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f)))
                        }
                    )
            )

            Text(
                text = formatTime(entry.endMin, is24Hour),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right 3D Gen Z Glass Card
        GlassPanel(
            hazeState = hazeState,
            modifier = Modifier
                .weight(1f)
                .animateContentSize(animationSpec = tween(300)),
            shape = GlassCardShape,
            shadowElevation = if (progressStatus is ClassProgressStatus.Ongoing) 16.dp else 10.dp,
            glassColor = if (progressStatus is ClassProgressStatus.Ongoing) accent else Color.White,
            glassAlphaTop = if (progressStatus is ClassProgressStatus.Ongoing) 0.28f else 0.18f,
            glassAlphaBottom = if (progressStatus is ClassProgressStatus.Ongoing) 0.12f else 0.08f,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                isExpanded = !isExpanded
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header Row: Accent Indicator + Subject + Live Status Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 5.dp, height = 38.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(accent)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = entry.subject,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${formatTime(entry.startMin, is24Hour)} – ${formatTime(entry.endMin, is24Hour)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    // Gen Z Real-Time Progress Badges
                    when (progressStatus) {
                        is ClassProgressStatus.Ongoing -> {
                            Box(
                                modifier = Modifier
                                    .scale(pulseScale)
                                    .clip(GlassPillShape)
                                    .background(accent.copy(alpha = 0.35f))
                                    .border(1.dp, accent, GlassPillShape)
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(accent)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "IN SESSION ⚡",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = accent
                                    )
                                }
                            }
                        }

                        is ClassProgressStatus.Finished -> {
                            Box(
                                modifier = Modifier
                                    .clip(GlassPillShape)
                                    .background(GlassSuccessColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Finished",
                                        tint = OnGlassSuccessColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Done ✨",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = OnGlassSuccessColor
                                    )
                                }
                            }
                        }

                        is ClassProgressStatus.Upcoming -> {
                            Box(
                                modifier = Modifier
                                    .clip(GlassPillShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, Color.White.copy(alpha = 0.25f), GlassPillShape)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "In ${progressStatus.formattedUntilStart} 🔥",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        ClassProgressStatus.OtherDay -> {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Ongoing Live Progress Bar & Precision Ticker
                if (progressStatus is ClassProgressStatus.Ongoing) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${(progressStatus.progressFraction * 100).toInt()}% Done",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "${progressStatus.formattedRemaining} left ⚡",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accent
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressStatus.progressFraction)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(accent, Color.White)
                                    )
                                )
                        )
                    }
                }

                // Expanded Info Section
                if (isExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))

                    if (entry.room.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Room: ${entry.room}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    if (entry.teacher.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Instructor: ${entry.teacher}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons (Edit & Delete)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(GlassErrorColor.copy(alpha = 0.2f))
                                .border(1.dp, GlassErrorColor.copy(alpha = 0.4f), GlassPillShape)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onDelete()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = OnGlassErrorColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Delete",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnGlassErrorColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), GlassPillShape)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onClick()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Edit Slot",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
