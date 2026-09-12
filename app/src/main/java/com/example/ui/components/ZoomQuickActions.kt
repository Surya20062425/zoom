package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZoomBlue
import com.example.ui.theme.ZoomOrange

@Composable
fun ZoomQuickActions(
    onNewMeetingClick: () -> Unit,
    onJoinClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onShareScreenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionButton(
            title = "New Meeting",
            icon = Icons.Default.Videocam,
            bgColor = ZoomOrange,
            testTag = "quick_action_new_meeting",
            onClick = onNewMeetingClick
        )

        QuickActionButton(
            title = "Join",
            icon = Icons.Default.Add,
            bgColor = ZoomBlue,
            testTag = "quick_action_join",
            onClick = onJoinClick
        )

        QuickActionButton(
            title = "Schedule",
            icon = Icons.Default.CalendarMonth,
            bgColor = ZoomBlue,
            testTag = "quick_action_schedule",
            onClick = onScheduleClick
        )

        QuickActionButton(
            title = "Share Screen",
            icon = Icons.Default.PresentToAll,
            bgColor = ZoomBlue,
            testTag = "quick_action_share",
            onClick = onShareScreenClick
        )
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(bgColor)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
