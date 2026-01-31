package com.rolenoteai.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rolenoteai.app.data.local.dao.*
import com.rolenoteai.app.data.local.entity.*

/**
 * RoleNote AI - Main Database
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Encrypted with SQLCipher
 * See: core/database/EncryptedDatabaseFactory.kt
 */
@Database(
    entities = [
        NoteEntity::class,
        MigrationEntity::class,
        ProjectEntity::class,
        ThreadEntity::class,
        RoleTemplateEntity::class,
        AuditLogEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class RoleNoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun projectDao(): ProjectDao
    abstract fun threadDao(): ThreadDao
    abstract fun roleTemplateDao(): RoleTemplateDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "rolenote_db"
    }
}
