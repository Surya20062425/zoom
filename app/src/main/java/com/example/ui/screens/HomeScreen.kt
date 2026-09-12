package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ZoomMeeting
import com.example.ui.ZoomMeetingViewModel
import com.example.ui.components.ZoomMainHeader
import com.example.ui.components.ZoomQuickActions
import com.example.ui.theme.ZoomBlue
import com.example.ui.theme.ZoomOrange
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: ZoomMeetingViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main Header
        ZoomMainHeader(
            currentUser = uiState.currentUser,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onProfileClick = { viewModel.setTab(com.example.ui.ZoomTab.SETTINGS) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            // 4 Signature Zoom Quick Action buttons
            item {
                ZoomQuickActions(
                    onNewMeetingClick = { viewModel.openNewMeetingDialog() },
                    onJoinClick = { viewModel.openJoinDialog() },
                    onScheduleClick = { viewModel.openScheduleDialog() },
                    onShareScreenClick = { viewModel.startInstantMeeting(videoOn = false, usePmi = false) }
                )
            }

            // Visual Hero Banner
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.3f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ZoomBlue.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "AI-POWERED WORKSPACE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZoomBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "High-Thinking Meetings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Instant summaries, real-time action items, and CameraX video rooms.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Image(
                            painter = painterResource(id = R.drawable.zoom_hero_banner),
                            contentDescription = "Zoom Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    }
                }
            }

            // Upcoming Meetings Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Upcoming Meetings",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "View All (${uiState.scheduledMeetings.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ZoomBlue,
                        modifier = Modifier.clickable { viewModel.setTab(com.example.ui.ZoomTab.MEETINGS) }
                    )
                }
            }

            val upcomingList = uiState.scheduledMeetings.take(3)
            if (upcomingList.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No upcoming meetings scheduled",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(upcomingList, key = { it.id }) { meeting ->
                    HomeMeetingCard(
                        meeting = meeting,
                        onStart = { viewModel.joinMeeting(meeting) }
                    )
                }
            }

            // Recent Team Chat Snippet
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Recent Team Chats",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Open Chats",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ZoomBlue,
                        modifier = Modifier.clickable { viewModel.setTab(com.example.ui.ZoomTab.TEAM_CHAT) }
                    )
                }
            }

            items(uiState.messages.take(2), key = { it.id }) { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Chat",
                            tint = ZoomBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = msg.senderName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = msg.message,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeMeetingCard(
    meeting: ZoomMeeting,
    onStart: () -> Unit
) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    val date = Date(meeting.scheduledTimeMillis)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${dateFormat.format(date)} • ${timeFormat.format(date)} (${meeting.durationMinutes}m)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZoomBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meeting.topic,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "ID: ${meeting.meetingNumber} • Pass: ${meeting.passcode}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = ZoomBlue),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_start_meeting_${meeting.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Start", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
