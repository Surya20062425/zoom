package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.gemini.AiResult
import com.example.ui.ZoomMeetingViewModel
import com.example.ui.dialogs.ThinkingAnimation
import com.example.ui.theme.ZoomBlue
import com.example.ui.theme.ZoomOrange
import com.example.ui.theme.ZoomRed

@Composable
fun AiAssistantScreen(
    viewModel: ZoomMeetingViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPromptMode by remember { mutableStateOf(0) } // 0: Meeting Agenda, 1: Deep Query, 2: Action Items
    var topicInput by remember { mutableStateOf("Global Infrastructure Scalability & Latency") }
    var detailInput by remember { mutableStateOf("Scale our real-time video WebRTC relays to support 500k concurrent participants with sub-50ms latency.") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Zoom AI Companion",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "gemini-3.1-pro-preview • High Thinking Mode",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ZoomBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Tabs
                TabRow(
                    selectedTabIndex = selectedPromptMode,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedPromptMode == 0,
                        onClick = { selectedPromptMode = 0 },
                        text = { Text("Agenda Architect", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedPromptMode == 1,
                        onClick = { selectedPromptMode = 1 },
                        text = { Text("Deep Reasoning", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedPromptMode == 2,
                        onClick = { selectedPromptMode = 2 },
                        text = { Text("Follow-up Brief", fontSize = 12.sp) }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp)
        ) {
            // Input Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (selectedPromptMode == 0) "Meeting Topic & Objectives" else "Complex Query or Discussion Context",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { topicInput = it },
                            label = { Text("Topic / Title") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_ai_topic")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = detailInput,
                            onValueChange = { detailInput = it },
                            label = { Text("Context & Constraints") },
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_ai_detail")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (selectedPromptMode == 0) {
                                    viewModel.generateStrategicAgenda(topicInput, detailInput, 45)
                                } else {
                                    viewModel.askAiQuestion("Topic: $topicInput\nDetails: $detailInput")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ZoomBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_ai_execute_high_thinking")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Analyze with High Thinking",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Results Section
            val activeState = if (selectedPromptMode == 0) uiState.aiAgendaState else uiState.aiChatState
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "AI Reasoning & Output",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = "THINKING: HIGH",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (activeState) {
                            is AiResult.Thinking -> {
                                ThinkingAnimation(message = activeState.message)
                            }
                            is AiResult.Success -> {
                                if (!activeState.thoughtProcess.isNullOrBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF3E5F5),
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = "🧠 High-Thinking Deliberation",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF6A1B9A)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = activeState.thoughtProcess,
                                                fontSize = 11.sp,
                                                color = Color(0xFF4A148C),
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = activeState.response,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            is AiResult.Error -> {
                                Text(
                                    text = "Error: ${activeState.message}",
                                    color = ZoomRed,
                                    fontSize = 13.sp
                                )
                            }
                            is AiResult.Idle -> {
                                Text(
                                    text = "Configure your topic and context above and tap 'Analyze with High Thinking'. Gemini 3.1 Pro will deeply deliberate on agenda priorities, architectural constraints, and deliver structured deliverables.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
