package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.model.MeetingSummary
import com.example.data.model.ZoomMeeting
import com.example.data.model.ZoomUser
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseService(private val context: Context) {

    private var firebaseAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    init {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            firebaseAuth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseService", "Firebase initialization check: ${e.message}")
        }
    }

    fun isFirebaseReady(): Boolean {
        return firebaseAuth != null && firestore != null
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        return try {
            firebaseAuth?.currentUser
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveMeeting(meeting: ZoomMeeting): Boolean {
        return try {
            val db = firestore
            if (db != null) {
                val map = hashMapOf(
                    "id" to meeting.id,
                    "topic" to meeting.topic,
                    "meetingNumber" to meeting.meetingNumber,
                    "passcode" to meeting.passcode,
                    "hostName" to meeting.hostName,
                    "hostEmail" to meeting.hostEmail,
                    "scheduledTimeMillis" to meeting.scheduledTimeMillis,
                    "durationMinutes" to meeting.durationMinutes,
                    "isVideoEnabled" to meeting.isVideoEnabled,
                    "isAudioEnabled" to meeting.isAudioEnabled,
                    "isWaitingRoomEnabled" to meeting.isWaitingRoomEnabled,
                    "usePmi" to meeting.usePmi,
                    "status" to meeting.status
                )
                db.collection("meetings").document(meeting.id).set(map).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error saving meeting to Firestore", e)
            false
        }
    }

    suspend fun deleteMeeting(meetingId: String): Boolean {
        return try {
            firestore?.collection("meetings")?.document(meetingId)?.delete()?.await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error deleting meeting from Firestore", e)
            false
        }
    }

    fun observeMeetings(): Flow<List<ZoomMeeting>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("meetings")
            .orderBy("scheduledTimeMillis", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirebaseService", "Firestore listen error: ${error.message}")
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ZoomMeeting(
                            id = doc.getString("id") ?: doc.id,
                            topic = doc.getString("topic") ?: "Meeting",
                            meetingNumber = doc.getString("meetingNumber") ?: "123 456 7890",
                            passcode = doc.getString("passcode") ?: "123456",
                            hostName = doc.getString("hostName") ?: "Host",
                            hostEmail = doc.getString("hostEmail") ?: "",
                            scheduledTimeMillis = doc.getLong("scheduledTimeMillis") ?: System.currentTimeMillis(),
                            durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 30,
                            isVideoEnabled = doc.getBoolean("isVideoEnabled") ?: true,
                            isAudioEnabled = doc.getBoolean("isAudioEnabled") ?: true,
                            isWaitingRoomEnabled = doc.getBoolean("isWaitingRoomEnabled") ?: true,
                            usePmi = doc.getBoolean("usePmi") ?: false,
                            status = doc.getString("status") ?: "upcoming"
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(list)
            }

        awaitClose {
            listener.remove()
        }
    }

    suspend fun saveMeetingSummary(summary: MeetingSummary): Boolean {
        return try {
            val db = firestore
            if (db != null) {
                val map = hashMapOf(
                    "meetingId" to summary.meetingId,
                    "topic" to summary.topic,
                    "dateString" to summary.dateString,
                    "executiveSummary" to summary.executiveSummary,
                    "keyDecisions" to summary.keyDecisions,
                    "actionItems" to summary.actionItems,
                    "deepThinkingNotes" to (summary.deepThinkingNotes ?: "")
                )
                db.collection("summaries").document(summary.meetingId).set(map).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error saving summary to Firestore", e)
            false
        }
    }

    suspend fun saveUserProfile(user: ZoomUser): Boolean {
        return try {
            val db = firestore
            if (db != null) {
                val map = hashMapOf(
                    "id" to user.id,
                    "name" to user.name,
                    "email" to user.email,
                    "avatarUrl" to (user.avatarUrl ?: ""),
                    "pmi" to user.pmi,
                    "isLicensed" to user.isLicensed
                )
                db.collection("users").document(user.id).set(map).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error saving user profile to Firestore", e)
            false
        }
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error signing out", e)
        }
    }
}
