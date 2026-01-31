package com.rolenoteai.app.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * RoleNote AI - Theme
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Material 3 theme with role-based accent colors
 */

// Primary colors
private val RoleNotePrimary = Color(0xFF3B82F6)  // Blue
private val RoleNoteSecondary = Color(0xFF8B5CF6)  // Purple
private val RoleNoteTertiary = Color(0xFF10B981)  // Green

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = RoleNotePrimary,
    secondary = RoleNoteSecondary,
    tertiary = RoleNoteTertiary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE1E1E1),
    onSurface = Color(0xFFE1E1E1)
)

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = RoleNotePrimary,
    secondary = RoleNoteSecondary,
    tertiary = RoleNoteTertiary,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A)
)

// Role-based accent colors
object RoleColors {
    // Functional roles
    val projectManager = Color(0xFF3B82F6)  // Blue
    val developer = Color(0xFF10B981)  // Green
    val accounting = Color(0xFF059669)  // Emerald
    val marketing = Color(0xFFEC4899)  // Pink
    val humanResources = Color(0xFFF59E0B)  // Amber
    val businessAdmin = Color(0xFF6366F1)  // Indigo
    val backendDev = Color(0xFF0EA5E9)  // Sky
    val frontendDev = Color(0xFF8B5CF6)  // Violet
    val customerService = Color(0xFF14B8A6)  // Teal
    val financialAdvisor = Color(0xFF10B981)  // Green
    val compliance = Color(0xFFEF4444)  // Red

    // C-Suite roles
    val ceo = Color(0xFFDC2626)  // Red
    val coo = Color(0xFF0891B2)  // Cyan
    val cto = Color(0xFF7C3AED)  // Purple
    val cfo = Color(0xFF059669)  // Emerald
    val cino = Color(0xFFF59E0B)  // Amber
    val cmoMonitor = Color(0xFFEF4444)  // Red
    val cro = Color(0xFF6366F1)  // Indigo
}

// Signifier colors
object SignifierColors {
    val task = Color(0xFF3B82F6)  // Blue •
    val event = Color(0xFF10B981)  // Green ○
    val note = Color(0xFF6B7280)  // Gray —
    val priority = Color(0xFFEF4444)  // Red !
    val explore = Color(0xFFF59E0B)  // Amber ?
    val idea = Color(0xFFFFD700)  // Gold *
    val done = Color(0xFF22C55E)  // Green ×
    val migrated = Color(0xFF8B5CF6)  // Purple >
    val scheduled = Color(0xFF0EA5E9)  // Sky <
    val cancelled = Color(0xFF9CA3AF)  // Gray ~
}

@Composable
fun RoleNoteAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
