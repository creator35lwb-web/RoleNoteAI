package com.rolenoteai.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * RoleNote AI - Main Application Class
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Entry point for the Android application
 * Initializes Hilt dependency injection
 */
@HiltAndroidApp
class RoleNoteAIApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Additional initialization can be added here
    }
}
