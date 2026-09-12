package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ZoomMeetingViewModel
import com.example.ui.ZoomTab
import com.example.ui.dialogs.JoinMeetingDialog
import com.example.ui.dialogs.NewMeetingDialog
import com.example.ui.dialogs.ScheduleMeetingDialog
import com.example.ui.screens.*
import com.example.ui.theme.ZoomBlue
import com.example.ui.theme.ZoomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZoomTheme {
                val viewModel: ZoomMeetingViewModel = viewModel()
                ZoomApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ZoomApp(
    viewModel: ZoomMeetingViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // If a meeting is currently active, render full-screen immersive ActiveMeetingScreen
    if (uiState.activeMeeting != null) {
        ActiveMeetingScreen(viewModel = viewModel)
    } else {
        Scaffold(
            bottomBar = {
                ZoomBottomNavigationBar(
                    currentTab = uiState.currentTab,
                    onTabSelected = { viewModel.setTab(it) }
                )
            },
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState.currentTab) {
                    ZoomTab.HOME -> HomeScreen(viewModel = viewModel)
                    ZoomTab.MEETINGS -> MeetingsListScreen(viewModel = viewModel)
                    ZoomTab.TEAM_CHAT -> TeamChatScreen(viewModel = viewModel)
                    ZoomTab.AI_COMPANION -> AiAssistantScreen(viewModel = viewModel)
                    ZoomTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }

            // Global Dialogs triggered from quick actions or headers
            if (uiState.isNewMeetingDialogOpen) {
                NewMeetingDialog(
                    userPmi = uiState.currentUser.pmi,
                    onStartMeeting = { videoOn, usePmi ->
                        viewModel.startInstantMeeting(videoOn = videoOn, usePmi = usePmi)
                    },
                    onDismiss = { viewModel.dismissAllModals() }
                )
            }

            if (uiState.isJoinDialogOpen) {
                JoinMeetingDialog(
                    initialName = uiState.currentUser.name,
                    onJoin = { meetingNumber, name, audioMuted, videoOff ->
                        viewModel.joinMeetingByNumber(
                            meetingNumber = meetingNumber,
                            name = name,
                            audioMuted = audioMuted,
                            videoOff = videoOff
                        )
                    },
                    onDismiss = { viewModel.dismissAllModals() }
                )
            }

            if (uiState.isScheduleDialogOpen) {
                ScheduleMeetingDialog(
                    onSchedule = { topic, startTime, duration, passcode, isWaitingRoom, isVideoOn ->
                        viewModel.scheduleMeeting(
                            topic = topic,
                            startTimeMillis = startTime,
                            durationMinutes = duration,
                            passcode = passcode,
                            isWaitingRoom = isWaitingRoom,
                            isVideoOn = isVideoOn
                        )
                    },
                    onDismiss = { viewModel.dismissAllModals() }
                )
            }
        }
    }
}

@Composable
fun ZoomBottomNavigationBar(
    currentTab: ZoomTab,
    onTabSelected: (ZoomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier.testTag("zoom_bottom_navigation")
    ) {
        NavigationBarItem(
            selected = currentTab == ZoomTab.HOME,
            onClick = { onTabSelected(ZoomTab.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Meet & Chat"
                )
            },
            label = {
                Text(
                    text = "Meet & Chat",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ZoomTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZoomBlue,
                selectedTextColor = ZoomBlue,
                indicatorColor = ZoomBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        NavigationBarItem(
            selected = currentTab == ZoomTab.MEETINGS,
            onClick = { onTabSelected(ZoomTab.MEETINGS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Meetings"
                )
            },
            label = {
                Text(
                    text = "Meetings",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ZoomTab.MEETINGS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZoomBlue,
                selectedTextColor = ZoomBlue,
                indicatorColor = ZoomBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_meetings")
        )

        NavigationBarItem(
            selected = currentTab == ZoomTab.TEAM_CHAT,
            onClick = { onTabSelected(ZoomTab.TEAM_CHAT) },
            icon = {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Team Chat"
                )
            },
            label = {
                Text(
                    text = "Team Chat",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ZoomTab.TEAM_CHAT) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZoomBlue,
                selectedTextColor = ZoomBlue,
                indicatorColor = ZoomBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_team_chat")
        )

        NavigationBarItem(
            selected = currentTab == ZoomTab.AI_COMPANION,
            onClick = { onTabSelected(ZoomTab.AI_COMPANION) },
            icon = {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Companion",
                    tint = if (currentTab == ZoomTab.AI_COMPANION) ZoomBlue else Color(0xFFFFB300)
                )
            },
            label = {
                Text(
                    text = "AI Companion",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ZoomTab.AI_COMPANION) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZoomBlue,
                selectedTextColor = ZoomBlue,
                indicatorColor = ZoomBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_ai_companion")
        )

        NavigationBarItem(
            selected = currentTab == ZoomTab.SETTINGS,
            onClick = { onTabSelected(ZoomTab.SETTINGS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            },
            label = {
                Text(
                    text = "Settings",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ZoomTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZoomBlue,
                selectedTextColor = ZoomBlue,
                indicatorColor = ZoomBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_settings")
        )
    }
}
