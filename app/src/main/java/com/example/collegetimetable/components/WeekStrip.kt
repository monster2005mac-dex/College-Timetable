package com.example.collegetimetable.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegetimetable.GlassPanel
import com.example.collegetimetable.WeekInfo
import dev.chrisbanes.haze.HazeState

@Composable
fun CurrentWeekStrip(
    weekInfo: WeekInfo,
    selectedDayVal: Int,
    accentColor: Color,
    hazeState: HazeState,
    todayCompletedCount: Int,
    todayTotalCount: Int,
    onDaySelected: (Int) -> Unit,
    onJumpToToday: () -> Unit,
    onOpenFullCalendar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // Month & Week Number Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onOpenFullCalendar)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Full Calendar",
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${weekInfo.monthYearLabel} • ${weekInfo.weekNumberLabel} ✨",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = 0.9f),
                    letterSpacing = 0.5.sp
                )
            }

            // "Jump to Today" Button
            val isTodaySelected = weekInfo.days.any { it.isToday && it.dayOfWeekValue == selectedDayVal }
            AnimatedVisibility(
                visible = !isTodaySelected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.25f))
                        .border(1.dp, accentColor, CircleShape)
                        .clickable(onClick = onJumpToToday)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "Jump to Today",
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Today ⚡",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
            }
        }

        // Horizontal Week Strip
        GlassPanel(
            hazeState = hazeState,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekInfo.days.forEach { dayInfo ->
                    val isSelected = selectedDayVal == dayInfo.dayOfWeekValue

                    val itemBg by animateColorAsState(
                        targetValue = if (isSelected) accentColor.copy(alpha = 0.35f) else Color.Transparent,
                        label = "strip_item_bg"
                    )

                    val itemBorder by animateColorAsState(
                        targetValue = when {
                            isSelected -> accentColor
                            dayInfo.isToday -> Color.White.copy(alpha = 0.6f)
                            else -> Color.Transparent
                        },
                        label = "strip_item_border"
                    )

                    val itemAlpha = if (dayInfo.isPast && !isSelected) 0.55f else 1.0f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .alpha(itemAlpha)
                            .clip(RoundedCornerShape(16.dp))
                            .background(itemBg)
                            .border(1.dp, itemBorder, RoundedCornerShape(16.dp))
                            .clickable { onDaySelected(dayInfo.dayOfWeekValue) }
                            .padding(vertical = 8.dp)
                    ) {
                        // Day letter ("Mon")
                        Text(
                            text = dayInfo.dayLetter,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Date Number ("16")
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (dayInfo.isToday && !isSelected) accentColor.copy(alpha = 0.25f)
                                    else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayInfo.dateNumber.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (dayInfo.isToday || isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected || dayInfo.isToday) Color.White else Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Class Indicator Dots (up to 3)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(6.dp)
                        ) {
                            if (dayInfo.classColors.isEmpty()) {
                                Spacer(modifier = Modifier.size(4.dp))
                            } else {
                                dayInfo.classColors.forEach { dotColor ->
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's Focus Progress Bar (slim glass bar)
        if (todayTotalCount > 0) {
            val progressFraction = (todayCompletedCount.toFloat() / todayTotalCount.toFloat()).coerceIn(0f, 1f)
            val animatedProgress by animateFloatAsState(targetValue = progressFraction, label = "day_progress")

            Spacer(modifier = Modifier.height(6.dp))

            GlassPanel(
                hazeState = hazeState,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp,
                glassAlphaTop = 0.10f,
                glassAlphaBottom = 0.05f
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Focus Meter ⚡",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$todayCompletedCount of $todayTotalCount done (${(progressFraction * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                    }
                }
            }
        }
    }
}
