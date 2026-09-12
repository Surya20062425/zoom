package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.theme.ZoomGreen
import com.example.ui.theme.ZoomRed

@Composable
fun MeetingTopBar(
    meetingTopic: String,
    elapsedSeconds: Int,
    isRecording: Boolean,
    onSwitchCamera: () -> Unit,
    onMeetingInfoClick: () -> Unit,
    onLeaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xCC11141A))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onSwitchCamera,
                modifier = Modifier.size(36.dp).testTag("meeting_switch_camera")
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.clickable(onClick = onMeetingInfoClick)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Zoom",
                        fontWeight = FontWeight.Bold,
                        color = ZoomBlue,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Meeting Info",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (isRecording) {
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ZoomRed)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "REC",
                        color = ZoomRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Leave / End Meeting Button (Red pill)
        Button(
            onClick = onLeaveClick,
            colors = ButtonDefaults.buttonColors(containerColor = ZoomRed),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp).testTag("meeting_leave_button")
        ) {
            Text(
                text = "Leave",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun MeetingBottomControls(
    isMuted: Boolean,
    isVideoOn: Boolean,
    isHandRaised: Boolean,
    isWhiteboardActive: Boolean,
    isScreenSharing: Boolean,
    participantCount: Int,
    onToggleMute: () -> Unit,
    onToggleVideo: () -> Unit,
    onToggleHandRaise: () -> Unit,
    onToggleWhiteboard: () -> Unit,
    onToggleScreenShare: () -> Unit,
    onParticipantsClick: () -> Unit,
    onChatClick: () -> Unit,
    onAiCompanionClick: () -> Unit,
    onEmojiClick: (String) -> Unit,
    onSecurityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showReactionsRow by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xF014161E))
    ) {
        // Optional quick reactions bar
        if (showReactionsRow) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F222C))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val emojis = listOf("👍", "👏", "❤️", "😂", "😮", "🎉", "✋")
                emojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .clickable {
                                onEmojiClick(emoji)
                                if (emoji == "✋") {
                                    onToggleHandRaise()
                                }
                            }
                            .padding(6.dp)
                    )
                }
            }
        }

        // Main controls horizontal scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute / Unmute
            MeetingControlItem(
                icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                label = if (isMuted) "Unmute" else "Mute",
                tint = if (isMuted) ZoomRed else Color.White,
                testTag = "control_mute_toggle",
                onClick = onToggleMute
            )

            // Video On / Off
            MeetingControlItem(
                icon = if (isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                label = if (isVideoOn) "Stop Video" else "Start Video",
                tint = if (isVideoOn) Color.White else ZoomRed,
                testTag = "control_video_toggle",
                onClick = onToggleVideo
            )

            // Share Content (Green Accent)
            MeetingControlItem(
                icon = Icons.Default.PresentToAll,
                label = if (isScreenSharing) "Stop Share" else "Share",
                tint = ZoomGreen,
                testTag = "control_share_toggle",
                onClick = onToggleScreenShare
            )

            // Interactive Whiteboard
            MeetingControlItem(
                icon = Icons.Default.Draw,
                label = "Whiteboard",
                tint = if (isWhiteboardActive) ZoomBlue else Color.White,
                testTag = "control_whiteboard_toggle",
                onClick = onToggleWhiteboard
            )

            // AI Companion (High Thinking)
            MeetingControlItem(
                icon = Icons.Default.AutoAwesome,
                label = "AI Companion",
                tint = Color(0xFFFFB300),
                testTag = "control_ai_companion",
                onClick = onAiCompanionClick
            )

            // Participants (with badge count)
            MeetingControlItem(
                icon = Icons.Default.People,
                label = "Participants",
                badgeText = participantCount.toString(),
                tint = Color.White,
                testTag = "control_participants",
                onClick = onParticipantsClick
            )

            // In-Meeting Chat
            MeetingControlItem(
                icon = Icons.Default.Chat,
                label = "Chat",
                tint = Color.White,
                testTag = "control_chat",
                onClick = onChatClick
            )

            // Reactions / Emoji toggle
            MeetingControlItem(
                icon = Icons.Default.Mood,
                label = "Reactions",
                tint = Color.White,
                testTag = "control_reactions",
                onClick = { showReactionsRow = !showReactionsRow }
            )

            // Security Settings
            MeetingControlItem(
                icon = Icons.Default.Security,
                label = "Security",
                tint = Color.White,
                testTag = "control_security",
                onClick = onSecurityClick
            )
        }
    }
}

@Composable
private fun MeetingControlItem(
    icon: ImageVector,
    label: String,
    tint: Color = Color.White,
    badgeText: String? = null,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )

            if (badgeText != null) {
                Surface(
                    shape = CircleShape,
                    color = ZoomBlue,
                    modifier = Modifier.offset(x = 6.dp, y = (-4).dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
