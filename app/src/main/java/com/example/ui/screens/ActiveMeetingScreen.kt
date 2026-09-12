package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.ZoomMeetingViewModel
import com.example.ui.components.*
import com.example.ui.dialogs.*
import com.example.ui.theme.ZoomGreen
import com.example.ui.theme.ZoomRed

@Composable
fun ActiveMeetingScreen(
    viewModel: ZoomMeetingViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeMeeting = uiState.activeMeeting ?: return
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1015))
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            MeetingTopBar(
                meetingTopic = activeMeeting.topic,
                elapsedSeconds = uiState.meetingElapsedSeconds,
                isRecording = uiState.isRecording,
                onSwitchCamera = { viewModel.flipCamera() },
                onMeetingInfoClick = { viewModel.openMeetingInfoDialog() },
                onLeaveClick = { viewModel.leaveMeeting() }
            )

            // Central Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                if (uiState.isWhiteboardActive) {
                    InteractiveWhiteboard(
                        strokes = uiState.whiteboardStrokes,
                        onAddStroke = { viewModel.addWhiteboardStroke(it) },
                        onClear = { viewModel.clearWhiteboard() },
                        onClose = { viewModel.toggleWhiteboard() }
                    )
                } else if (uiState.isScreenSharing) {
                    // Screen Sharing Presentation Canvas
                    ScreenSharingMockView(
                        presenterName = uiState.currentUser.name,
                        onStopShare = { viewModel.toggleScreenShare() }
                    )
                } else {
                    // Multi-Participant Grid
                    ParticipantVideoGrid(
                        participants = uiState.participants,
                        localUserId = uiState.currentUser.id,
                        isFrontCamera = uiState.isFrontCamera
                    )
                }

                // Floating Reactions Burst Layer
                FloatingReactionsOverlay(
                    floatingEmojis = uiState.floatingEmojis,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom Controls Bar
            MeetingBottomControls(
                isMuted = uiState.isMuted,
                isVideoOn = uiState.isVideoOn,
                isHandRaised = uiState.isHandRaised,
                isWhiteboardActive = uiState.isWhiteboardActive,
                isScreenSharing = uiState.isScreenSharing,
                participantCount = uiState.participants.size,
                onToggleMute = { viewModel.toggleMute() },
                onToggleVideo = { viewModel.toggleVideo() },
                onToggleHandRaise = { viewModel.toggleHandRaise() },
                onToggleWhiteboard = { viewModel.toggleWhiteboard() },
                onToggleScreenShare = { viewModel.toggleScreenShare() },
                onParticipantsClick = { viewModel.openParticipantsSheet() },
                onChatClick = { viewModel.openChatSheet() },
                onAiCompanionClick = { viewModel.openAiCompanionSheet() },
                onEmojiClick = { viewModel.sendEmojiReaction(it) },
                onSecurityClick = { viewModel.openSecuritySheet() }
            )
        }

        // Dialogs & Sheets
        if (uiState.isMeetingInfoDialogOpen) {
            MeetingInfoDialog(
                meeting = activeMeeting,
                onDismiss = { viewModel.dismissAllModals() }
            )
        }

        if (uiState.isParticipantsSheetOpen) {
            ParticipantsBottomSheet(
                participants = uiState.participants,
                onDismiss = { viewModel.dismissAllModals() },
                onMuteAll = { viewModel.toggleMute() }
            )
        }

        if (uiState.isChatSheetOpen) {
            InMeetingChatBottomSheet(
                messages = uiState.messages,
                onSendMessage = { viewModel.sendChatMessage(it) },
                onDismiss = { viewModel.dismissAllModals() }
            )
        }

        if (uiState.isAiCompanionSheetOpen) {
            AiCompanionBottomSheet(
                topic = activeMeeting.topic,
                transcript = uiState.transcript,
                summaryState = uiState.aiSummaryState,
                chatState = uiState.aiChatState,
                onRequestSummary = { viewModel.requestMeetingSummary() },
                onAskQuestion = { viewModel.askAiQuestion(it) },
                onDismiss = { viewModel.dismissAllModals() }
            )
        }

        if (uiState.isSecuritySheetOpen) {
            SecurityBottomSheet(onDismiss = { viewModel.dismissAllModals() })
        }
    }
}

@Composable
private fun ParticipantVideoGrid(
    participants: List<com.example.data.model.MeetingParticipant>,
    localUserId: String,
    isFrontCamera: Boolean
) {
    if (participants.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val topRow = participants.take(2)
        val bottomRow = participants.drop(2).take(2)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topRow.forEach { participant ->
                ParticipantVideoTile(
                    participant = participant,
                    isLocalUser = (participant.id == localUserId || participant.isHost),
                    isFrontCamera = isFrontCamera,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        if (bottomRow.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bottomRow.forEach { participant ->
                    ParticipantVideoTile(
                        participant = participant,
                        isLocalUser = false,
                        isFrontCamera = isFrontCamera,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenSharingMockView(
    presenterName: String,
    onStopShare: () -> Unit
) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        color = Color(0xFF161922),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PresentToAll,
                contentDescription = "Sharing",
                tint = ZoomGreen,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You are sharing your screen",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "All attendees can see your presentation in high definition.",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onStopShare,
                colors = ButtonDefaults.buttonColors(containerColor = ZoomRed),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("Stop Sharing Screen", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
