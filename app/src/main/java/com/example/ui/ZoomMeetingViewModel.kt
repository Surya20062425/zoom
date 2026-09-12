package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirebaseService
import com.example.data.firebase.GoogleAuthManager
import com.example.data.gemini.AiResult
import com.example.data.gemini.GeminiMeetingService
import com.example.data.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class ZoomTab {
    HOME,
    MEETINGS,
    TEAM_CHAT,
    AI_COMPANION,
    SETTINGS
}

data class FloatingEmoji(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val startXFraction: Float
)

data class ZoomUiState(
    val currentUser: ZoomUser = ZoomUser(),
    val currentTab: ZoomTab = ZoomTab.HOME,
    val scheduledMeetings: List<ZoomMeeting> = emptyList(),
    val pastSummaries: List<MeetingSummary> = emptyList(),
    // Active meeting state
    val activeMeeting: ZoomMeeting? = null,
    val isInMeeting: Boolean = false,
    val isMuted: Boolean = false,
    val isVideoOn: Boolean = true,
    val isFrontCamera: Boolean = true,
    val isHandRaised: Boolean = false,
    val isScreenSharing: Boolean = false,
    val isWhiteboardActive: Boolean = false,
    val selectedVirtualBg: String? = null,
    val participants: List<MeetingParticipant> = emptyList(),
    val messages: List<ZoomChatMessage> = emptyList(),
    val transcript: List<TranscriptItem> = emptyList(),
    val whiteboardStrokes: List<WhiteboardStroke> = emptyList(),
    val floatingEmojis: List<FloatingEmoji> = emptyList(),
    val activeSpeakerName: String = "Sarah Connor",
    val meetingElapsedSeconds: Int = 0,
    val isRecording: Boolean = false,
    // Sheets & Dialogs
    val isNewMeetingDialogOpen: Boolean = false,
    val isJoinDialogOpen: Boolean = false,
    val isScheduleDialogOpen: Boolean = false,
    val isMeetingInfoDialogOpen: Boolean = false,
    val isParticipantsSheetOpen: Boolean = false,
    val isChatSheetOpen: Boolean = false,
    val isAiCompanionSheetOpen: Boolean = false,
    val isSecuritySheetOpen: Boolean = false,
    // AI state
    val aiSummaryState: AiResult = AiResult.Idle,
    val aiChatState: AiResult = AiResult.Idle,
    val aiAgendaState: AiResult = AiResult.Idle,
    val toastMessage: String? = null
)

class ZoomMeetingViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseService = FirebaseService(application)
    private val googleAuthManager = GoogleAuthManager(application)
    private val geminiService = GeminiMeetingService()

    private val _uiState = MutableStateFlow(ZoomUiState())
    val uiState: StateFlow<ZoomUiState> = _uiState.asStateFlow()

    private var meetingTimerJob: Job? = null
    private var simulatedDialogueJob: Job? = null

    init {
        loadDefaultMeetings()
        loadDefaultTeamChats()
        observeFirestoreMeetings()
    }

    private fun loadDefaultMeetings() {
        val now = System.currentTimeMillis()
        val defaultList = listOf(
            ZoomMeeting(
                id = "mtg_sync_daily",
                topic = "Weekly Product & Architecture Review",
                meetingNumber = "491 823 1094",
                passcode = "940218",
                hostName = "Sarah Connor",
                hostEmail = "sarah.connor@example.com",
                scheduledTimeMillis = now + 1800000, // 30 min from now
                durationMinutes = 45,
                isVideoEnabled = true,
                isAudioEnabled = true
            ),
            ZoomMeeting(
                id = "mtg_client_demo",
                topic = "Client Mobile App Experience Walkthrough",
                meetingNumber = "820 193 4572",
                passcode = "332145",
                hostName = "Alex Chen",
                hostEmail = "alex.chen@example.com",
                scheduledTimeMillis = now + 7200000, // 2h from now
                durationMinutes = 30,
                isVideoEnabled = true,
                isAudioEnabled = true
            ),
            ZoomMeeting(
                id = "mtg_standup",
                topic = "Sprint Retro & Planning",
                meetingNumber = "319 028 9912",
                passcode = "102938",
                hostName = "David Kim",
                hostEmail = "david.kim@example.com",
                scheduledTimeMillis = now + 86400000, // tomorrow
                durationMinutes = 60,
                isVideoEnabled = true,
                isAudioEnabled = true
            )
        )
        _uiState.update { it.copy(scheduledMeetings = defaultList) }
    }

    private fun loadDefaultTeamChats() {
        val defaultMessages = listOf(
            ZoomChatMessage(
                id = "msg_1",
                senderName = "Alex Chen",
                senderId = "usr_alex",
                message = "Hey team, looking forward to the sync today!"
            ),
            ZoomChatMessage(
                id = "msg_2",
                senderName = "Sarah Connor",
                senderId = "usr_sarah",
                message = "I have the new UI mockups ready to present via screen share."
            ),
            ZoomChatMessage(
                id = "msg_3",
                senderName = "David Kim",
                senderId = "usr_david",
                message = "Awesome! Backend latency is down 40% in staging."
            )
        )
        _uiState.update { it.copy(messages = defaultMessages) }
    }

    private fun observeFirestoreMeetings() {
        viewModelScope.launch {
            try {
                firebaseService.observeMeetings().collect { firestoreMeetings ->
                    if (firestoreMeetings.isNotEmpty()) {
                        _uiState.update { current ->
                            val combined = (firestoreMeetings + current.scheduledMeetings)
                                .distinctBy { it.id }
                                .sortedBy { it.scheduledTimeMillis }
                            current.copy(scheduledMeetings = combined)
                        }
                    }
                }
            } catch (e: Exception) {
                // fallback handled
            }
        }
    }

    fun setTab(tab: ZoomTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            val result = googleAuthManager.signInWithGoogle()
            result.onSuccess { user ->
                _uiState.update { it.copy(currentUser = user, toastMessage = "Signed in as ${user.email}") }
                firebaseService.saveUserProfile(user)
            }.onFailure { err ->
                _uiState.update { it.copy(toastMessage = "Sign in: ${err.message}") }
            }
        }
    }

    fun signOut() {
        firebaseService.signOut()
        _uiState.update {
            it.copy(
                currentUser = ZoomUser(
                    id = "guest_user",
                    name = "Guest User",
                    email = "guest@zoom.us",
                    isLicensed = false,
                    isGoogleUser = false
                ),
                toastMessage = "Signed out"
            )
        }
    }

    // --- Meeting Launch & Join Operations ---

    fun startInstantMeeting(videoOn: Boolean, usePmi: Boolean) {
        val user = _uiState.value.currentUser
        val meetingId = if (usePmi) user.pmi else generateMeetingNumber()
        val meeting = ZoomMeeting(
            id = "mtg_${UUID.randomUUID()}",
            topic = "${user.name}'s Personal Meeting Room",
            meetingNumber = meetingId,
            passcode = generatePasscode(),
            hostName = user.name,
            hostEmail = user.email,
            scheduledTimeMillis = System.currentTimeMillis(),
            durationMinutes = 60,
            isVideoEnabled = videoOn,
            isAudioEnabled = true,
            usePmi = usePmi,
            status = "active"
        )
        joinMeeting(meeting, startVideoOn = videoOn)
        dismissAllModals()
    }

    fun joinMeetingByNumber(meetingNumber: String, name: String, audioMuted: Boolean, videoOff: Boolean) {
        val meeting = ZoomMeeting(
            id = "mtg_joined_${UUID.randomUUID()}",
            topic = "Zoom Meeting $meetingNumber",
            meetingNumber = meetingNumber.ifEmpty { "849 203 1928" },
            passcode = "123456",
            hostName = "Host",
            hostEmail = "host@zoom.us",
            scheduledTimeMillis = System.currentTimeMillis(),
            durationMinutes = 45,
            isVideoEnabled = !videoOff,
            isAudioEnabled = !audioMuted,
            status = "active"
        )
        joinMeeting(meeting, startVideoOn = !videoOff, startMuted = audioMuted)
        dismissAllModals()
    }

    fun joinMeeting(meeting: ZoomMeeting, startVideoOn: Boolean = true, startMuted: Boolean = false) {
        val user = _uiState.value.currentUser
        val initialParticipants = listOf(
            MeetingParticipant(
                id = user.id,
                name = "${user.name} (Me)",
                role = "Host",
                isMuted = startMuted,
                isVideoOn = startVideoOn,
                isHost = true
            ),
            MeetingParticipant(
                id = "p_sarah",
                name = "Sarah Connor",
                role = "Lead Designer",
                isMuted = false,
                isVideoOn = true,
                isSpeaking = true,
                videoBg = "office"
            ),
            MeetingParticipant(
                id = "p_alex",
                name = "Alex Chen",
                role = "Product Manager",
                isMuted = true,
                isVideoOn = true,
                videoBg = "cafe"
            ),
            MeetingParticipant(
                id = "p_david",
                name = "David Kim",
                role = "Staff Engineer",
                isMuted = false,
                isVideoOn = true
            )
        )

        val sampleTranscript = listOf(
            TranscriptItem("t_1", "Sarah Connor", "Good morning everyone! Let's kick off with the high-priority architecture deliverables."),
            TranscriptItem("t_2", "Alex Chen", "I've reviewed the customer metrics. The meeting UI responsiveness is our top target."),
            TranscriptItem("t_3", "David Kim", "Agreed. We reduced latency by migrating our media pipeline to WebRTC with edge relays.")
        )

        _uiState.update {
            it.copy(
                activeMeeting = meeting,
                isInMeeting = true,
                isMuted = startMuted,
                isVideoOn = startVideoOn,
                participants = initialParticipants,
                transcript = sampleTranscript,
                meetingElapsedSeconds = 0,
                isWhiteboardActive = false,
                isScreenSharing = false,
                isHandRaised = false,
                floatingEmojis = emptyList(),
                activeSpeakerName = "Sarah Connor",
                aiSummaryState = AiResult.Idle
            )
        }

        startMeetingTimer()
        startSimulatedDialogue()
    }

    fun leaveMeeting() {
        meetingTimerJob?.cancel()
        simulatedDialogueJob?.cancel()
        _uiState.update {
            it.copy(
                activeMeeting = null,
                isInMeeting = false,
                isWhiteboardActive = false,
                isScreenSharing = false,
                isHandRaised = false,
                isParticipantsSheetOpen = false,
                isChatSheetOpen = false,
                isAiCompanionSheetOpen = false,
                isSecuritySheetOpen = false,
                isMeetingInfoDialogOpen = false,
                whiteboardStrokes = emptyList()
            )
        }
    }

    fun scheduleMeeting(
        topic: String,
        startTimeMillis: Long,
        durationMinutes: Int,
        passcode: String,
        isWaitingRoom: Boolean,
        isVideoOn: Boolean
    ) {
        val user = _uiState.value.currentUser
        val meeting = ZoomMeeting(
            id = "mtg_${UUID.randomUUID()}",
            topic = topic.ifBlank { "Team Meeting" },
            meetingNumber = generateMeetingNumber(),
            passcode = passcode.ifBlank { generatePasscode() },
            hostName = user.name,
            hostEmail = user.email,
            scheduledTimeMillis = startTimeMillis,
            durationMinutes = durationMinutes,
            isVideoEnabled = isVideoOn,
            isAudioEnabled = true,
            isWaitingRoomEnabled = isWaitingRoom,
            status = "upcoming"
        )

        _uiState.update { current ->
            current.copy(
                scheduledMeetings = (listOf(meeting) + current.scheduledMeetings).sortedBy { it.scheduledTimeMillis },
                toastMessage = "Meeting scheduled: $topic"
            )
        }

        viewModelScope.launch {
            firebaseService.saveMeeting(meeting)
        }
        dismissAllModals()
    }

    fun deleteMeeting(meetingId: String) {
        _uiState.update { current ->
            current.copy(
                scheduledMeetings = current.scheduledMeetings.filterNot { it.id == meetingId },
                toastMessage = "Meeting cancelled"
            )
        }
        viewModelScope.launch {
            firebaseService.deleteMeeting(meetingId)
        }
    }

    // --- In-Meeting Toggles ---

    fun toggleMute() {
        _uiState.update { current ->
            val newMute = !current.isMuted
            val updatedParticipants = current.participants.map { p ->
                if (p.isHost) p.copy(isMuted = newMute) else p
            }
            current.copy(isMuted = newMute, participants = updatedParticipants)
        }
    }

    fun toggleVideo() {
        _uiState.update { current ->
            val newVideo = !current.isVideoOn
            val updatedParticipants = current.participants.map { p ->
                if (p.isHost) p.copy(isVideoOn = newVideo) else p
            }
            current.copy(isVideoOn = newVideo, participants = updatedParticipants)
        }
    }

    fun flipCamera() {
        _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun toggleHandRaise() {
        _uiState.update { current ->
            val newHand = !current.isHandRaised
            val updatedParticipants = current.participants.map { p ->
                if (p.isHost) p.copy(isHandRaised = newHand) else p
            }
            current.copy(
                isHandRaised = newHand,
                participants = updatedParticipants,
                toastMessage = if (newHand) "Hand raised" else "Hand lowered"
            )
        }
    }

    fun toggleScreenShare() {
        _uiState.update {
            val nextState = !it.isScreenSharing
            it.copy(
                isScreenSharing = nextState,
                isWhiteboardActive = false,
                toastMessage = if (nextState) "Screen sharing started" else "Screen sharing stopped"
            )
        }
    }

    fun toggleWhiteboard() {
        _uiState.update {
            val nextState = !it.isWhiteboardActive
            it.copy(
                isWhiteboardActive = nextState,
                isScreenSharing = false,
                toastMessage = if (nextState) "Interactive Whiteboard open" else "Whiteboard closed"
            )
        }
    }

    fun setVirtualBackground(bgName: String?) {
        _uiState.update { current ->
            val updatedParticipants = current.participants.map { p ->
                if (p.isHost) p.copy(videoBg = bgName) else p
            }
            current.copy(
                selectedVirtualBg = bgName,
                participants = updatedParticipants,
                toastMessage = if (bgName != null) "Virtual background applied" else "Virtual background removed"
            )
        }
    }

    fun toggleRecording() {
        _uiState.update {
            val next = !it.isRecording
            it.copy(
                isRecording = next,
                toastMessage = if (next) "Recording in progress..." else "Recording stopped. Processing AI transcript."
            )
        }
    }

    fun sendEmojiReaction(emoji: String) {
        val newReaction = FloatingEmoji(
            emoji = emoji,
            startXFraction = (0.2f + Math.random().toFloat() * 0.6f)
        )
        _uiState.update { current ->
            current.copy(floatingEmojis = current.floatingEmojis + newReaction)
        }
        viewModelScope.launch {
            delay(2800)
            _uiState.update { current ->
                current.copy(floatingEmojis = current.floatingEmojis.filterNot { it.id == newReaction.id })
            }
        }
    }

    fun addWhiteboardStroke(stroke: WhiteboardStroke) {
        _uiState.update { it.copy(whiteboardStrokes = it.whiteboardStrokes + stroke) }
    }

    fun clearWhiteboard() {
        _uiState.update { it.copy(whiteboardStrokes = emptyList()) }
    }

    fun sendChatMessage(text: String, recipient: String = "Everyone") {
        if (text.isBlank()) return
        val user = _uiState.value.currentUser
        val newMsg = ZoomChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = user.name,
            senderId = user.id,
            message = text.trim(),
            recipient = recipient,
            isDirect = recipient != "Everyone"
        )
        _uiState.update { it.copy(messages = it.messages + newMsg) }
    }

    // --- Gemini 3.1 Pro High Thinking Features ---

    fun requestMeetingSummary() {
        val currentTopic = _uiState.value.activeMeeting?.topic ?: "Team Sync"
        val transcriptText = _uiState.value.transcript.joinToString("\n") {
            "${it.speaker}: ${it.text}"
        }

        _uiState.update { it.copy(aiSummaryState = AiResult.Thinking()) }

        viewModelScope.launch {
            val result = geminiService.generateMeetingSummary(currentTopic, transcriptText)
            result.onSuccess { (summaryContent, thinkingThoughts) ->
                val summaryObj = MeetingSummary(
                    meetingId = _uiState.value.activeMeeting?.id ?: UUID.randomUUID().toString(),
                    topic = currentTopic,
                    dateString = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
                    executiveSummary = summaryContent,
                    keyDecisions = listOf("Architecture v2 approved", "Launch target set for Q3"),
                    actionItems = listOf("Sarah: UI specs", "David: Staging patch", "Alex: Stakeholder brief"),
                    deepThinkingNotes = thinkingThoughts
                )

                _uiState.update { current ->
                    current.copy(
                        aiSummaryState = AiResult.Success(summaryContent, thinkingThoughts),
                        pastSummaries = listOf(summaryObj) + current.pastSummaries
                    )
                }

                firebaseService.saveMeetingSummary(summaryObj)
            }.onFailure { err ->
                _uiState.update { it.copy(aiSummaryState = AiResult.Error(err.message ?: "Failed to generate summary")) }
            }
        }
    }

    fun askAiQuestion(query: String) {
        if (query.isBlank()) return
        val currentTopic = _uiState.value.activeMeeting?.topic ?: "General Discussion"
        val transcriptText = _uiState.value.transcript.joinToString("\n") {
            "${it.speaker}: ${it.text}"
        }

        _uiState.update { it.copy(aiChatState = AiResult.Thinking()) }

        viewModelScope.launch {
            val result = geminiService.answerMeetingQuestion(query, currentTopic, transcriptText)
            result.onSuccess { (answer, thoughts) ->
                _uiState.update { it.copy(aiChatState = AiResult.Success(answer, thoughts)) }
            }.onFailure { err ->
                _uiState.update { it.copy(aiChatState = AiResult.Error(err.message ?: "Error reasoning query")) }
            }
        }
    }

    fun generateStrategicAgenda(topic: String, objectives: String, duration: Int) {
        _uiState.update { it.copy(aiAgendaState = AiResult.Thinking()) }

        viewModelScope.launch {
            val result = geminiService.generateSmartAgenda(topic, objectives, duration)
            result.onSuccess { (agenda, thoughts) ->
                _uiState.update { it.copy(aiAgendaState = AiResult.Success(agenda, thoughts)) }
            }.onFailure { err ->
                _uiState.update { it.copy(aiAgendaState = AiResult.Error(err.message ?: "Error generating agenda")) }
            }
        }
    }

    // --- Modal Sheets Management ---

    fun openNewMeetingDialog() = _uiState.update { it.copy(isNewMeetingDialogOpen = true) }
    fun openJoinDialog() = _uiState.update { it.copy(isJoinDialogOpen = true) }
    fun openScheduleDialog() = _uiState.update { it.copy(isScheduleDialogOpen = true) }
    fun openMeetingInfoDialog() = _uiState.update { it.copy(isMeetingInfoDialogOpen = true) }
    fun openParticipantsSheet() = _uiState.update { it.copy(isParticipantsSheetOpen = true) }
    fun openChatSheet() = _uiState.update { it.copy(isChatSheetOpen = true) }
    fun openAiCompanionSheet() = _uiState.update { it.copy(isAiCompanionSheetOpen = true) }
    fun openSecuritySheet() = _uiState.update { it.copy(isSecuritySheetOpen = true) }

    fun dismissAllModals() {
        _uiState.update {
            it.copy(
                isNewMeetingDialogOpen = false,
                isJoinDialogOpen = false,
                isScheduleDialogOpen = false,
                isMeetingInfoDialogOpen = false,
                isParticipantsSheetOpen = false,
                isChatSheetOpen = false,
                isAiCompanionSheetOpen = false,
                isSecuritySheetOpen = false
            )
        }
    }

    fun clearToast() = _uiState.update { it.copy(toastMessage = null) }

    // --- Internal Timers & Dialogue Simulation ---

    private fun startMeetingTimer() {
        meetingTimerJob?.cancel()
        meetingTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uiState.update { it.copy(meetingElapsedSeconds = it.meetingElapsedSeconds + 1) }
            }
        }
    }

    private fun startSimulatedDialogue() {
        simulatedDialogueJob?.cancel()
        val phrases = listOf(
            Pair("Sarah Connor", "Let's review the user retention curve from last week's cohort."),
            Pair("David Kim", "Latency in the Asia-Pacific region dropped below 45ms after the edge deploy."),
            Pair("Alex Chen", "Customers are specifically requesting automatic AI notes and action item delivery."),
            Pair("Sarah Connor", "Let's definitely use Gemini High Thinking for the meeting synthesis!"),
            Pair("David Kim", "I'll submit the PR for the new security encryption handshakes today.")
        )

        simulatedDialogueJob = viewModelScope.launch {
            var index = 0
            while (isActive) {
                delay(14000) // periodic simulated transcript entry
                if (index < phrases.size) {
                    val item = phrases[index]
                    val transcriptEntry = TranscriptItem(
                        id = UUID.randomUUID().toString(),
                        speaker = item.first,
                        text = item.second
                    )
                    _uiState.update { current ->
                        val updatedParticipants = current.participants.map { p ->
                            p.copy(isSpeaking = (p.name.contains(item.first)))
                        }
                        current.copy(
                            transcript = current.transcript + transcriptEntry,
                            activeSpeakerName = item.first,
                            participants = updatedParticipants
                        )
                    }
                    index++
                }
            }
        }
    }

    private fun generateMeetingNumber(): String {
        val p1 = (100..999).random()
        val p2 = (1000..9999).random()
        val p3 = (1000..9999).random()
        return "$p1 $p2 $p3"
    }

    private fun generatePasscode(): String {
        return (100000..999999).random().toString()
    }
}
