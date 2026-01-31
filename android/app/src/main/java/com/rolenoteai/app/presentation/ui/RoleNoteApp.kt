package com.rolenoteai.app.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rolenoteai.app.presentation.ui.screens.*
import com.rolenoteai.app.presentation.viewmodel.AuthViewModel

/**
 * RoleNote AI - Main App Composable
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Navigation and bottom bar setup
 */

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    data object Overview : Screen("overview", "Today", { Icon(Icons.Default.Home, contentDescription = "Today") })
    data object Notes : Screen("notes", "Notes", { Icon(Icons.Default.List, contentDescription = "Notes") })
    data object Capture : Screen("capture", "Capture", { Icon(Icons.Default.Add, contentDescription = "Capture") })
    data object Chat : Screen("chat", "AI Chat", { Icon(Icons.Default.Chat, contentDescription = "AI Chat") })
    data object Settings : Screen("settings", "Settings", { Icon(Icons.Default.Settings, contentDescription = "Settings") })

    // Non-bottom-bar screens
    data object Auth : Screen("auth", "Authentication", { })
    data object Setup : Screen("setup", "Setup", { })
    data object NoteDetail : Screen("note/{noteId}", "Note", { })
    data object ProjectDetail : Screen("project/{projectId}", "Project", { })
    data object TemplateSelect : Screen("template-select", "Select Role", { })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleNoteApp(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    val bottomBarScreens = listOf(
        Screen.Overview,
        Screen.Notes,
        Screen.Capture,
        Screen.Chat,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine start destination based on auth state
    val startDestination = when (authState) {
        is com.rolenoteai.app.core.security.AuthenticationManager.AuthState.SetupRequired -> Screen.Setup.route
        is com.rolenoteai.app.core.security.AuthenticationManager.AuthState.Locked,
        is com.rolenoteai.app.core.security.AuthenticationManager.AuthState.LockedOut -> Screen.Auth.route
        is com.rolenoteai.app.core.security.AuthenticationManager.AuthState.Unlocked -> Screen.Overview.route
    }

    val showBottomBar = currentRoute in bottomBarScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon() },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth screens
            composable(Screen.Auth.route) {
                AuthScreen(
                    onAuthenticated = {
                        navController.navigate(Screen.Overview.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    },
                    onSetupRequired = {
                        navController.navigate(Screen.Setup.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Setup.route) {
                SetupScreen(
                    onSetupComplete = {
                        navController.navigate(Screen.TemplateSelect.route) {
                            popUpTo(Screen.Setup.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.TemplateSelect.route) {
                TemplateSelectScreen(
                    onTemplateSelected = {
                        navController.navigate(Screen.Overview.route) {
                            popUpTo(Screen.TemplateSelect.route) { inclusive = true }
                        }
                    }
                )
            }

            // Main screens
            composable(Screen.Overview.route) {
                OverviewScreen(
                    onNoteClick = { noteId ->
                        navController.navigate("note/$noteId")
                    },
                    onCaptureClick = {
                        navController.navigate(Screen.Capture.route)
                    }
                )
            }

            composable(Screen.Notes.route) {
                NotesScreen(
                    onNoteClick = { noteId ->
                        navController.navigate("note/$noteId")
                    }
                )
            }

            composable(Screen.Capture.route) {
                CaptureScreen(
                    onNoteSaved = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Chat.route) {
                ChatScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Detail screens
            composable(Screen.NoteDetail.route) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
                NoteDetailScreen(
                    noteId = noteId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.ProjectDetail.route) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
                ProjectDetailScreen(
                    projectId = projectId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
