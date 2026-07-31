package com.noshad.wallclock

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityManager
import android.widget.TextClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private lateinit var clock: TextClock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        statusText = findViewById(R.id.statusText)
        clock = findViewById(R.id.clock)

        clock.format12Hour = "hh:mm"
        clock.format24Hour = "HH:mm"
        
        statusText.text = "Connected"

        checkAndEnableService()
    }

    override fun onResume() {
        super.onResume()
        checkAndEnableService()
    }

    private fun checkAndEnableService() {
        if (!isAccessibilityServiceEnabled()) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASKS)
        return enabledServices.any { it.id.contains("com.noshad.wallclock") }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
