package com.rolenoteai.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rolenoteai.app.core.security.AuthenticationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * RoleNote AI - Authentication ViewModel
 * CTO: RNA (Claude Code Opus 4.5)
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthenticationManager
) : ViewModel() {

    val authState: StateFlow<AuthenticationManager.AuthState> = authManager.authState

    init {
        viewModelScope.launch {
            authManager.initialize()
        }
    }

    fun authenticateWithPin(pin: String): AuthenticationManager.AuthResult {
        return authManager.authenticateWithPin(pin)
    }

    fun setupPin(pin: String): AuthenticationManager.AuthResult {
        return authManager.setupPin(pin)
    }

    fun isBiometricAvailable(): Boolean {
        return authManager.isBiometricAvailable()
    }

    fun isBiometricEnabled(): Boolean {
        return authManager.isBiometricEnabled()
    }

    fun isPinSet(): Boolean {
        return authManager.isPinSet()
    }

    fun lock() {
        authManager.lock()
    }

    fun updateActivity() {
        authManager.updateLastActivity()
    }

    fun checkAutoLock() {
        authManager.checkAutoLock()
    }
}
