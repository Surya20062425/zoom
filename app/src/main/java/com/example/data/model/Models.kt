package com.example.data.model

data class ZoomUser(
    val id: String = "usr_google_1",
    val name: String = "User",
    val email: String = "b.7993974026@gmail.com",
    val avatarUrl: String? = null,
    val pmi: String = "734-829-1094",
    val isLicensed: Boolean = true,
    val isGoogleUser: Boolean = true
)

data class ZoomMeeting(
    val id: String = "",
    val topic: String = "Team Sync",
    val meetingNumber: String = "892 4102 3841",
    val passcode: String = "749210",
    val hostName: String = "Host",
    val hostEmail: String = "b.7993974026@gmail.com",
    val scheduledTimeMillis: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 45,
    val isVideoEnabled: Boolean = true,
    val isAudioEnabled: Boolean = true,
    val isWaitingRoomEnabled: Boolean = true,
    val usePmi: Boolean = false,
    val status: String = "upcoming" // upcoming, active, ended
)

data class MeetingParticipant(
    val id: String,
    val name: String,
    val role: String = "Participant",
    val isMuted: Boolean = true,
    val isVideoOn: Boolean = true,
    val isHandRaised: Boolean = false,
    val isSpeaking: Boolean = false,
    val isHost: Boolean = false,
    val videoBg: String? = null // "office", "cafe", or null
)

data class ZoomChatMessage(
    val id: String,
    val senderName: String,
    val senderId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDirect: Boolean = false,
    val recipient: String = "Everyone"
)

data class TranscriptItem(
    val id: String,
    val speaker: String,
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class MeetingSummary(
    val meetingId: String,
    val topic: String,
    val dateString: String,
    val executiveSummary: String,
    val keyDecisions: List<String>,
    val actionItems: List<String>,
    val deepThinkingNotes: String? = null
)

data class DrawingPoint(
    val x: Float,
    val y: Float
)

data class WhiteboardStroke(
    val points: List<DrawingPoint>,
    val colorHex: Long = 0xFF0E71EB,
    val strokeWidth: Float = 6f
)
