package com.rolenoteai.app.domain.model

import java.util.UUID

/**
 * RoleNote AI - Domain Models
 * CTO: RNA (Claude Code Opus 4.5)
 * Phase 3b: Core Engine
 */

// ==================== Signifier ====================

/**
 * BuJo-inspired signifiers for quick note categorization
 */
enum class Signifier(
    val symbol: String,
    val displayName: String,
    val description: String,
    val isActionable: Boolean
) {
    TASK("•", "Task", "Action item to complete", true),
    EVENT("○", "Event", "Calendar event or meeting", true),
    NOTE("—", "Note", "General information", false),
    PRIORITY("!", "Priority", "High importance item", true),
    EXPLORE("?", "Explore", "Research or investigate", true),
    IDEA("*", "Idea", "Creative thought for later", false),
    DONE("×", "Done", "Completed task", false),
    MIGRATED(">", "Migrated", "Moved to future date", false),
    SCHEDULED("<", "Scheduled", "Added to calendar", false),
    CANCELLED("~", "Cancelled", "No longer relevant", false);

    companion object {
        fun fromSymbol(symbol: String): Signifier? {
            return entries.find { it.symbol == symbol }
        }

        fun fromInput(input: String): Signifier {
            return when (input.trim().lowercase()) {
                ".", "-", "task" -> TASK
                "o", "event" -> EVENT
                "--", "note" -> NOTE
                "!", "priority", "important" -> PRIORITY
                "?", "explore", "research" -> EXPLORE
                "*", "idea" -> IDEA
                else -> NOTE
            }
        }

        val actionableSignifiers = entries.filter { it.isActionable }
        val allSymbols = entries.map { it.symbol }
    }
}

// ==================== Note Status ====================

enum class NoteStatus(val value: String) {
    OPEN("open"),
    DONE("done"),
    MIGRATED("migrated"),
    SCHEDULED("scheduled"),
    CANCELLED("cancelled");

    companion object {
        fun fromValue(value: String): NoteStatus {
            return entries.find { it.value == value } ?: OPEN
        }
    }
}

// ==================== Note ====================

/**
 * Domain model for a note
 */
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String? = null,
    val content: String,
    val signifier: Signifier,
    val status: NoteStatus = NoteStatus.OPEN,
    val roleTemplateId: String? = null,
    val roleTemplateName: String? = null,
    val projectId: String? = null,
    val threadId: String? = null,
    val tags: List<String> = emptyList(),
    val migrationCount: Int = 0,
    val scheduledFor: Long? = null,
    val aiSummary: String? = null,
    val aiSuggestions: List<String> = emptyList(),
    val audioFilePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    val isActionable: Boolean
        get() = signifier.isActionable && status == NoteStatus.OPEN

    val displayContent: String
        get() = "${signifier.symbol} $content"

    val isOverdue: Boolean
        get() = scheduledFor != null && scheduledFor < System.currentTimeMillis() && status == NoteStatus.OPEN
}

// ==================== Project ====================

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val color: String = "#3B82F6",
    val icon: String = "folder",
    val status: String = "active",
    val deadline: Long? = null,
    val noteCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ==================== Thread ====================

data class Thread(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val noteCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ==================== Role Template ====================

/**
 * Role template configuration
 */
data class RoleTemplate(
    val id: String,
    val name: String,
    val version: String = "1.0",
    val description: String? = null,
    val category: TemplateCategory,
    val icon: String,
    val color: String,
    val isBuiltIn: Boolean = true,
    val isActive: Boolean = false,
    val capturePrompts: List<CapturePrompt> = emptyList(),
    val suggestionRules: List<SuggestionRule> = emptyList(),
    val executionSettings: ExecutionSettings = ExecutionSettings()
)

enum class TemplateCategory(val value: String, val displayName: String) {
    FUNCTIONAL("functional", "Functional Roles"),
    C_SUITE("c-suite", "C-Suite Roles"),
    CUSTOM("custom", "Custom Templates");

    companion object {
        fun fromValue(value: String): TemplateCategory {
            return entries.find { it.value == value } ?: CUSTOM
        }
    }
}

data class CapturePrompt(
    val field: String,
    val prompt: String,
    val required: Boolean = false
)

data class SuggestionRule(
    val trigger: String,
    val action: String,
    val priority: Int = 0
)

data class ExecutionSettings(
    val signifiersEnabled: Boolean = true,
    val defaultSignifier: String = "task",
    val migrationPromptDays: List<Int> = listOf(3, 7, 14),
    val weeklyReview: Boolean = true,
    val monthlyReview: Boolean = true,
    val autoThreading: Boolean = true,
    val staleTaskThresholdDays: Int = 5
)

// ==================== Reminder ====================

data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val noteId: String? = null,
    val projectId: String? = null,
    val title: String,
    val message: String? = null,
    val triggerType: ReminderTriggerType,
    val triggerTime: Long? = null,
    val isRecurring: Boolean = false,
    val status: ReminderStatus = ReminderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ReminderTriggerType(val value: String) {
    TIME("time"),
    EVENT("event"),
    CONTEXT("context"),
    MIGRATION("migration");

    companion object {
        fun fromValue(value: String): ReminderTriggerType {
            return entries.find { it.value == value } ?: TIME
        }
    }
}

enum class ReminderStatus(val value: String) {
    PENDING("pending"),
    TRIGGERED("triggered"),
    DISMISSED("dismissed"),
    SNOOZED("snoozed");

    companion object {
        fun fromValue(value: String): ReminderStatus {
            return entries.find { it.value == value } ?: PENDING
        }
    }
}

// ==================== Audit Log ====================

data class AuditLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val entityType: String,
    val entityId: String,
    val actionType: AuditActionType,
    val actionDetails: String? = null,
    val previousValue: String? = null,
    val newValue: String? = null,
    val aiModel: String? = null,
    val userAccepted: Boolean? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AuditActionType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    AI_SUGGEST("ai_suggest"),
    AI_ACCEPT("ai_accept"),
    AI_REJECT("ai_reject");

    companion object {
        fun fromValue(value: String): AuditActionType {
            return entries.find { it.value == value } ?: CREATE
        }
    }
}
