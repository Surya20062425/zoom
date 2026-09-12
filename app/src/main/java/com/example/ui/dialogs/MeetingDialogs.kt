package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ZoomMeeting
import com.example.ui.theme.ZoomBlue
import com.example.ui.theme.ZoomGreen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewMeetingDialog(
    userPmi: String,
    onStartMeeting: (videoOn: Boolean, usePmi: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var videoOn by remember { mutableStateOf(true) }
    var usePmi by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Start a Meeting",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Video On",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = videoOn,
                        onCheckedChange = { videoOn = it },
                        modifier = Modifier.testTag("switch_video_on")
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Use Personal Meeting ID",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = userPmi,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = usePmi,
                        onCheckedChange = { usePmi = it },
                        modifier = Modifier.testTag("switch_use_pmi")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onStartMeeting(videoOn, usePmi) },
                    colors = ButtonDefaults.buttonColors(containerColor = ZoomBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_start_instant_meeting")
                ) {
                    Text(
                        text = "Start a Meeting",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun JoinMeetingDialog(
    initialName: String,
    onJoin: (meetingNumber: String, name: String, audioMuted: Boolean, videoOff: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var meetingNumber by remember { mutableStateOf("") }
    var participantName by remember { mutableStateOf(initialName) }
    var dontConnectAudio by remember { mutableStateOf(false) }
    var turnOffVideo by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Join a Meeting",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = meetingNumber,
                    onValueChange = { meetingNumber = it },
                    label = { Text("Meeting ID or Personal Link Name") },
                    placeholder = { Text("e.g. 892 4102 3841") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_join_meeting_id")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = participantName,
                    onValueChange = { participantName = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_join_name")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "JOIN OPTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Don't connect to audio", fontSize = 14.sp)
                    Switch(
                        checked = dontConnectAudio,
                        onCheckedChange = { dontConnectAudio = it },
                        modifier = Modifier.testTag("switch_dont_connect_audio")
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Turn off my video", fontSize = 14.sp)
                    Switch(
                        checked = turnOffVideo,
                        onCheckedChange = { turnOffVideo = it },
                        modifier = Modifier.testTag("switch_turn_off_video")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onJoin(meetingNumber, participantName, dontConnectAudio, turnOffVideo) },
                    colors = ButtonDefaults.buttonColors(containerColor = ZoomBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_confirm_join")
                ) {
                    Text(
                        text = "Join",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleMeetingDialog(
    onSchedule: (topic: String, startTimeMillis: Long, durationMinutes: Int, passcode: String, isWaitingRoom: Boolean, isVideoOn: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var topic by remember { mutableStateOf("Weekly Sync") }
    var durationMinutes by remember { mutableStateOf(45) }
    var passcode by remember { mutableStateOf("749210") }
    var isWaitingRoom by remember { mutableStateOf(true) }
    var isVideoOn by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Schedule Meeting",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Meeting Topic") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_schedule_topic")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passcode,
                    onValueChange = { passcode = it },
                    label = { Text("Passcode") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_schedule_passcode")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Duration: $durationMinutes minutes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    listOf(15, 30, 45, 60).forEach { mins ->
                        FilterChip(
                            selected = durationMinutes == mins,
                            onClick = { durationMinutes = mins },
                            label = { Text("${mins}m") }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enable Waiting Room", fontSize = 14.sp)
                    Switch(checked = isWaitingRoom, onCheckedChange = { isWaitingRoom = it })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Host Video On", fontSize = 14.sp)
                    Switch(checked = isVideoOn, onCheckedChange = { isVideoOn = it })
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val startTime = System.currentTimeMillis() + 3600000 // 1 hour from now
                        onSchedule(topic, startTime, durationMinutes, passcode, isWaitingRoom, isVideoOn)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZoomBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_confirm_schedule")
                ) {
                    Text(
                        text = "Save to Firestore",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MeetingInfoDialog(
    meeting: ZoomMeeting,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = meeting.topic,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                InfoRow(label = "Meeting ID", value = meeting.meetingNumber)
                InfoRow(label = "Passcode", value = meeting.passcode)
                InfoRow(label = "Host", value = meeting.hostName)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Encrypted",
                        tint = ZoomGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Enhanced End-to-End Encryption",
                        fontSize = 12.sp,
                        color = ZoomGreen,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Zoom Invite", "Join Zoom Meeting: https://zoom.us/j/${meeting.meetingNumber.replace(" ", "")}?pwd=${meeting.passcode}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Invitation link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_copy_meeting_link")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Link",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Invitation Link")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
