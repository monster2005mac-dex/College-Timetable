package com.example.collegetimetable.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegetimetable.GlassPanel
import dev.chrisbanes.haze.HazeState

enum class NavigationDestination(val label: String, val icon: ImageVector) {
    TODAY("Today", Icons.Default.Today),
    WEEK("Week", Icons.Default.GridView),
    ATTENDANCE("Bunks", Icons.Default.CheckCircle),
    EXAMS("Exams", Icons.Default.Assignment),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun GlassBottomBar(
    currentDestination: NavigationDestination,
    hazeState: HazeState,
    accentColor: Color,
    onDestinationSelected: (NavigationDestination) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        GlassPanel(
            hazeState = hazeState,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 16.dp,
            glassAlphaTop = 0.28f,
            glassAlphaBottom = 0.12f
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination

                    val itemBg by animateColorAsState(
                        targetValue = if (isSelected) accentColor.copy(alpha = 0.35f) else Color.Transparent,
                        label = "nav_item_bg"
                    )

                    val itemBorder by animateColorAsState(
                        targetValue = if (isSelected) accentColor else Color.Transparent,
                        label = "nav_item_border"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(itemBg)
                            .border(1.dp, itemBorder, CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onDestinationSelected(destination)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label,
                            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(18.dp)
                        )

                        if (isSelected) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = destination.label,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
