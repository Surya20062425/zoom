package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.camera.CameraPreview
import com.example.data.model.MeetingParticipant
import com.example.ui.theme.ZoomBlue
import com.example.ui.theme.ZoomGreen
import com.example.ui.theme.ZoomRed

@Composable
fun ParticipantVideoTile(
    participant: MeetingParticipant,
    isLocalUser: Boolean,
    isFrontCamera: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Pulse border when active speaker
    val infiniteTransition = rememberInfiniteTransition(label = "speaker_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val borderModifier = if (participant.isSpeaking && !participant.isMuted) {
        Modifier.border(2.5.dp, ZoomGreen.copy(alpha = pulseAlpha), RoundedCornerShape(16.dp))
    } else {
        Modifier.border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(borderModifier)
            .background(Color(0xFF1B1E26))
    ) {
        // Video content layer
        if (participant.isVideoOn) {
            if (isLocalUser) {
                CameraPreview(
                    isFrontCamera = isFrontCamera,
                    isVideoEnabled = true,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Remote participant video simulation / background
                if (participant.videoBg == "office") {
                    Image(
                        painter = painterResource(id = R.drawable.virtual_bg_office),
                        contentDescription = "Virtual Office",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (participant.videoBg == "cafe") {
                    Image(
                        painter = painterResource(id = R.drawable.virtual_bg_cafe),
                        contentDescription = "Virtual Cafe",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF282C37), Color(0xFF181B22))
                                )
                            )
                    )
                }

                // Participant video avatar silhouette overlay
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(ZoomBlue.copy(alpha = 0.85f))
                            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Text(
                            text = participant.name.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Video Off Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E222B))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333845))
                ) {
                    Text(
                        text = participant.name.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Hand raised badge
        if (participant.isHandRaised) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFB300),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PanTool,
                        contentDescription = "Hand Raised",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Hand Raised", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        // Speaking audio wave / indicator badge at top-right
        if (participant.isSpeaking && !participant.isMuted) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ZoomGreen.copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Speaking",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Speaking", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Bottom label bar with Participant Name & Mic Status
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.Black.copy(alpha = 0.65f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (participant.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (participant.isMuted) "Muted" else "Unmuted",
                    tint = if (participant.isMuted) ZoomRed else Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = participant.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
