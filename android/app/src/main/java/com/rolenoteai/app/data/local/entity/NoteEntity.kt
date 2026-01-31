package com.rolenoteai.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * RoleNote AI - Note Entity
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Core entity for storing notes with BuJo-inspired signifiers and execution tracking
 */
@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["status"]),
        Index(value = ["signifier"]),
        Index(value = ["roleTemplateId"]),
        Index(value = ["projectId"]),
        Index(value = ["threadId"]),
        Index(value = ["createdAt"]),
        Index(value = ["scheduledFor"])
    ]
)
data class NoteEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    // Core content
    val title: String?,
    val content: String,
    val signifier: String,  // •, ○, —, !, ?, *, ×, >, <, ~

    // Role context
    val roleTemplateId: String?,
    val roleTemplateName: String?,

    // Execution tracking (BuJo-inspired)
    val status: String = "open",  // open, done, migrated, scheduled, cancelled
    val migrationCount: Int = 0,
    val scheduledFor: Long? = null,

    // Threading (linking related notes)
    val threadId: String? = null,
    val projectId: String? = null,

    // Tags
    val tags: String? = null,  // Comma-separated

    // AI-generated content
    val aiSummary: String? = null,
    val aiSuggestions: String? = null,  // JSON array

    // Embedding vector (stored as JSON array for simplicity)
    val embedding: String? = null,

    // Audio reference (if captured via voice)
    val audioFilePath: String? = null,
    val audioTranscription: String? = null,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

/**
 * Migration history entity
 */
@Entity(
    tableName = "migrations",
    indices = [Index(value = ["noteId"])]
)
data class MigrationEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val noteId: String,
    val originalDate: Long,
    val migrationDate: Long,
    val action: String,  // migrated, scheduled, cancelled
    val newDate: Long? = null,
    val reason: String? = null
)

/**
 * Project entity for grouping notes
 */
@Entity(
    tableName = "projects",
    indices = [Index(value = ["status"])]
)
data class ProjectEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val color: String = "#3B82F6",
    val icon: String = "folder",
    val status: String = "active",  // active, completed, archived
    val deadline: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Thread entity for linking related notes across time
 */
@Entity(tableName = "threads")
data class ThreadEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val noteCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Role template entity (user-customized templates)
 */
@Entity(tableName = "role_templates")
data class RoleTemplateEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val category: String,  // functional, c-suite, custom
    val icon: String,
    val color: String,
    val isBuiltIn: Boolean = false,
    val isActive: Boolean = false,  // Currently selected template
    val configJson: String,  // Full template configuration
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Audit log entity for tracking AI actions (Z-Guardian requirement)
 */
@Entity(
    tableName = "audit_log",
    indices = [
        Index(value = ["entityType", "entityId"]),
        Index(value = ["actionType"]),
        Index(value = ["timestamp"])
    ]
)
data class AuditLogEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val entityType: String,  // note, project, reminder, ai_suggestion
    val entityId: String,
    val actionType: String,  // create, update, delete, ai_suggest, ai_accept, ai_reject
    val actionDetails: String?,  // JSON with details
    val previousValue: String?,  // For updates
    val newValue: String?,  // For updates
    val aiModel: String? = null,  // Which AI model made suggestion
    val userAccepted: Boolean? = null,  // Did user accept AI suggestion
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Reminder entity
 */
@Entity(
    tableName = "reminders",
    indices = [
        Index(value = ["noteId"]),
        Index(value = ["triggerTime"]),
        Index(value = ["status"])
    ]
)
data class ReminderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val noteId: String?,
    val projectId: String?,
    val title: String,
    val message: String?,
    val triggerType: String,  // time, event, context, migration
    val triggerTime: Long?,
    val triggerEventId: String?,  // Calendar event ID
    val triggerContext: String?,  // Project/location context
    val isRecurring: Boolean = false,
    val recurringRule: String? = null,  // RRULE format
    val status: String = "pending",  // pending, triggered, dismissed, snoozed
    val createdAt: Long = System.currentTimeMillis()
)
