package com.rolenoteai.app.core.security

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RoleNote AI - Authentication Manager
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Implements security specs from: docs/security/AUTHENTICATION.md
 * Trinity Validation ID: fa3e7b66
 *
 * Features:
 * - Biometric authentication (fingerprint/face)
 * - PIN fallback
 * - Session management
 * - Auto-lock on inactivity
 */
@Singleton
class AuthenticationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PREFS_NAME = "rolenoteai_auth_prefs"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_AUTH_ENABLED = "auth_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_LAST_ACTIVITY = "last_activity"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"

        // Security settings
        const val MAX_FAILED_ATTEMPTS = 5
        const val LOCKOUT_DURATION_MS = 30_000L // 30 seconds
        const val AUTO_LOCK_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes
        const val MIN_PIN_LENGTH = 4
        const val MAX_PIN_LENGTH = 8
    }

    // ==================== State ====================

    sealed class AuthState {
        data object Locked : AuthState()
        data object Unlocked : AuthState()
        data object SetupRequired : AuthState()
        data class LockedOut(val remainingMs: Long) : AuthState()
    }

    sealed class AuthResult {
        data object Success : AuthResult()
        data class Failed(val reason: String) : AuthResult()
        data class LockedOut(val remainingMs: Long) : AuthResult()
        data object Cancelled : AuthResult()
        data object BiometricNotAvailable : AuthResult()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Locked)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val encryptedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // ==================== Initialization ====================

    fun initialize() {
        val authEnabled = encryptedPrefs.getBoolean(KEY_AUTH_ENABLED, false)
        val hasPinSet = encryptedPrefs.getString(KEY_PIN_HASH, null) != null

        _authState.value = when {
            !authEnabled && !hasPinSet -> AuthState.SetupRequired
            checkLockout() != null -> AuthState.LockedOut(checkLockout()!!)
            else -> AuthState.Locked
        }
    }

    // ==================== Biometric ====================

    /**
     * Check if biometric authentication is available
     */
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    /**
     * Check if biometric is enabled by user
     */
    fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false) && isBiometricAvailable()
    }

    /**
     * Enable/disable biometric authentication
     */
    fun setBiometricEnabled(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    /**
     * Authenticate using biometrics
     */
    fun authenticateWithBiometric(
        activity: FragmentActivity,
        onResult: (AuthResult) -> Unit
    ) {
        if (!isBiometricAvailable()) {
            onResult(AuthResult.BiometricNotAvailable)
            return
        }

        val lockoutRemaining = checkLockout()
        if (lockoutRemaining != null) {
            onResult(AuthResult.LockedOut(lockoutRemaining))
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthSuccess()
                onResult(AuthResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        onResult(AuthResult.Cancelled)
                    }
                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                        onResult(AuthResult.LockedOut(LOCKOUT_DURATION_MS))
                    }
                    else -> {
                        onResult(AuthResult.Failed(errString.toString()))
                    }
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                incrementFailedAttempts()
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("RoleNote AI")
            .setSubtitle("Authenticate to access your notes")
            .setNegativeButtonText("Use PIN")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // ==================== PIN ====================

    /**
     * Check if PIN is set
     */
    fun isPinSet(): Boolean {
        return encryptedPrefs.getString(KEY_PIN_HASH, null) != null
    }

    /**
     * Set up a new PIN
     */
    fun setupPin(pin: String): AuthResult {
        if (pin.length < MIN_PIN_LENGTH || pin.length > MAX_PIN_LENGTH) {
            return AuthResult.Failed("PIN must be $MIN_PIN_LENGTH-$MAX_PIN_LENGTH digits")
        }

        if (!pin.all { it.isDigit() }) {
            return AuthResult.Failed("PIN must contain only digits")
        }

        // Check for weak PINs
        if (isWeakPin(pin)) {
            return AuthResult.Failed("PIN is too weak. Avoid sequential or repeated digits.")
        }

        val pinHash = hashPin(pin)
        encryptedPrefs.edit()
            .putString(KEY_PIN_HASH, pinHash)
            .putBoolean(KEY_AUTH_ENABLED, true)
            .apply()

        onAuthSuccess()
        return AuthResult.Success
    }

    /**
     * Authenticate with PIN
     */
    fun authenticateWithPin(pin: String): AuthResult {
        val lockoutRemaining = checkLockout()
        if (lockoutRemaining != null) {
            return AuthResult.LockedOut(lockoutRemaining)
        }

        val storedHash = encryptedPrefs.getString(KEY_PIN_HASH, null)
            ?: return AuthResult.Failed("PIN not set up")

        val inputHash = hashPin(pin)

        return if (inputHash == storedHash) {
            onAuthSuccess()
            AuthResult.Success
        } else {
            val attempts = incrementFailedAttempts()
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                startLockout()
                AuthResult.LockedOut(LOCKOUT_DURATION_MS)
            } else {
                AuthResult.Failed("Incorrect PIN. ${MAX_FAILED_ATTEMPTS - attempts} attempts remaining.")
            }
        }
    }

    /**
     * Change PIN (requires current PIN verification)
     */
    fun changePin(currentPin: String, newPin: String): AuthResult {
        val verifyResult = authenticateWithPin(currentPin)
        if (verifyResult !is AuthResult.Success) {
            return verifyResult
        }

        // Lock again before setting new PIN
        _authState.value = AuthState.Locked

        return setupPin(newPin)
    }

    // ==================== Session Management ====================

    /**
     * Called on successful authentication
     */
    private fun onAuthSuccess() {
        resetFailedAttempts()
        updateLastActivity()
        _authState.value = AuthState.Unlocked
    }

    /**
     * Lock the app
     */
    fun lock() {
        _authState.value = AuthState.Locked
    }

    /**
     * Update last activity timestamp
     */
    fun updateLastActivity() {
        encryptedPrefs.edit()
            .putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
            .apply()
    }

    /**
     * Check if auto-lock should trigger
     */
    fun shouldAutoLock(): Boolean {
        if (_authState.value != AuthState.Unlocked) return false

        val lastActivity = encryptedPrefs.getLong(KEY_LAST_ACTIVITY, 0L)
        val elapsed = System.currentTimeMillis() - lastActivity

        return elapsed > AUTO_LOCK_TIMEOUT_MS
    }

    /**
     * Check auto-lock and lock if needed
     */
    fun checkAutoLock() {
        if (shouldAutoLock()) {
            lock()
        }
    }

    // ==================== Lockout Management ====================

    private fun checkLockout(): Long? {
        val lockoutEnd = encryptedPrefs.getLong("lockout_end", 0L)
        if (lockoutEnd == 0L) return null

        val remaining = lockoutEnd - System.currentTimeMillis()
        return if (remaining > 0) {
            _authState.value = AuthState.LockedOut(remaining)
            remaining
        } else {
            encryptedPrefs.edit().remove("lockout_end").apply()
            null
        }
    }

    private fun startLockout() {
        val lockoutEnd = System.currentTimeMillis() + LOCKOUT_DURATION_MS
        encryptedPrefs.edit().putLong("lockout_end", lockoutEnd).apply()
        _authState.value = AuthState.LockedOut(LOCKOUT_DURATION_MS)
    }

    private fun incrementFailedAttempts(): Int {
        val attempts = encryptedPrefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
        encryptedPrefs.edit().putInt(KEY_FAILED_ATTEMPTS, attempts).apply()
        return attempts
    }

    private fun resetFailedAttempts() {
        encryptedPrefs.edit()
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .remove("lockout_end")
            .apply()
    }

    // ==================== Helpers ====================

    private fun hashPin(pin: String): String {
        // Add a device-specific salt
        val salt = Build.FINGERPRINT + Build.DEVICE
        val input = pin + salt

        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))

        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun isWeakPin(pin: String): Boolean {
        // Check for repeated digits (1111, 0000)
        if (pin.all { it == pin[0] }) return true

        // Check for sequential digits (1234, 4321)
        val digits = pin.map { it.digitToInt() }
        val isAscending = digits.zipWithNext().all { (a, b) -> b - a == 1 }
        val isDescending = digits.zipWithNext().all { (a, b) -> a - b == 1 }
        if (isAscending || isDescending) return true

        // Check for common weak PINs
        val weakPins = setOf("1234", "0000", "1111", "1212", "7777", "1004", "2000", "4444", "2222", "6969")
        if (pin in weakPins) return true

        return false
    }

    /**
     * Reset all authentication data (for testing/logout)
     */
    fun resetAuth() {
        encryptedPrefs.edit().clear().apply()
        _authState.value = AuthState.SetupRequired
    }
}
