package com.rolenoteai.app.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RoleNote AI - Encrypted Database Factory
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Implements security specs from: docs/security/THREAT_MODEL.md
 * Trinity Validation ID: fa3e7b66
 *
 * Features:
 * - SQLCipher encryption for all local data
 * - Secure key generation and storage
 * - Database migration support
 */
@Singleton
class EncryptedDatabaseFactory @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val KEY_PREFS_NAME = "rolenoteai_db_keys"
        private const val KEY_DB_PASSPHRASE = "db_passphrase"
        private const val PASSPHRASE_LENGTH = 64
    }

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val keyPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            KEY_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Get or generate the database encryption passphrase
     */
    private fun getOrCreatePassphrase(): ByteArray {
        val existingPassphrase = keyPrefs.getString(KEY_DB_PASSPHRASE, null)

        return if (existingPassphrase != null) {
            existingPassphrase.toByteArray(Charsets.UTF_8)
        } else {
            // Generate a new secure passphrase
            val passphrase = generateSecurePassphrase()
            keyPrefs.edit()
                .putString(KEY_DB_PASSPHRASE, String(passphrase, Charsets.UTF_8))
                .apply()
            passphrase
        }
    }

    /**
     * Generate a cryptographically secure passphrase
     */
    private fun generateSecurePassphrase(): ByteArray {
        val random = SecureRandom()
        val passphrase = ByteArray(PASSPHRASE_LENGTH)
        random.nextBytes(passphrase)
        return passphrase
    }

    /**
     * Create SQLCipher SupportFactory for Room
     */
    fun createSupportFactory(): SupportFactory {
        SQLiteDatabase.loadLibs(context)
        val passphrase = getOrCreatePassphrase()
        return SupportFactory(passphrase)
    }

    /**
     * Build an encrypted Room database
     */
    fun <T : RoomDatabase> buildDatabase(
        databaseClass: Class<T>,
        name: String,
        migrations: (RoomDatabase.Builder<T>) -> RoomDatabase.Builder<T> = { it }
    ): T {
        val factory = createSupportFactory()

        val builder = Room.databaseBuilder(
            context.applicationContext,
            databaseClass,
            name
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()

        return migrations(builder).build()
    }

    /**
     * Check if database exists and is accessible
     */
    fun isDatabaseAccessible(name: String): Boolean {
        return try {
            val dbFile = context.getDatabasePath(name)
            if (!dbFile.exists()) return true // Will be created

            // Try to open with current passphrase
            val passphrase = String(getOrCreatePassphrase(), Charsets.UTF_8)
            SQLiteDatabase.loadLibs(context)
            val db = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                passphrase,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            db.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Re-key database with new passphrase (for key rotation)
     */
    fun rekeyDatabase(name: String): Boolean {
        return try {
            val dbFile = context.getDatabasePath(name)
            if (!dbFile.exists()) return true

            val oldPassphrase = String(getOrCreatePassphrase(), Charsets.UTF_8)
            val newPassphrase = generateSecurePassphrase()

            SQLiteDatabase.loadLibs(context)
            val db = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                oldPassphrase,
                null,
                SQLiteDatabase.OPEN_READWRITE
            )

            // Re-key the database
            db.rawExecSQL("PRAGMA rekey = '${String(newPassphrase, Charsets.UTF_8)}';")
            db.close()

            // Store new passphrase
            keyPrefs.edit()
                .putString(KEY_DB_PASSPHRASE, String(newPassphrase, Charsets.UTF_8))
                .apply()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Delete database and its passphrase (for logout/reset)
     */
    fun deleteDatabase(name: String): Boolean {
        return try {
            val dbFile = context.getDatabasePath(name)
            val walFile = context.getDatabasePath("$name-wal")
            val shmFile = context.getDatabasePath("$name-shm")

            dbFile.delete()
            walFile.delete()
            shmFile.delete()

            keyPrefs.edit().remove(KEY_DB_PASSPHRASE).apply()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
