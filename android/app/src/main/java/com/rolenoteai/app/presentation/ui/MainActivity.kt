package com.rolenoteai.app.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rolenoteai.app.presentation.ui.theme.RoleNoteAITheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * RoleNote AI - Main Activity
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Entry point for the Android UI
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RoleNoteAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoleNoteApp()
                }
            }
        }
    }
}
