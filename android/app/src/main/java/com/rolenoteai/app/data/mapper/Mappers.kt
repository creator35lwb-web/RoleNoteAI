package com.rolenoteai.app.data.mapper

import com.rolenoteai.app.data.local.entity.*
import com.rolenoteai.app.domain.model.*

/**
 * RoleNote AI - Entity to Domain Mappers
 * CTO: RNA (Claude Code Opus 4.5)
 * Phase 3b: Core Engine
 */

// ==================== Note Mappers ====================

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        signifier = Signifier.fromSymbol(signifier) ?: Signifier.NOTE,
        status = NoteStatus.fromValue(status),
        roleTemplateId = roleTemplateId,
        roleTemplateName = roleTemplateName,
        projectId = projectId,
        threadId = threadId,
        tags = tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        migrationCount = migrationCount,
        scheduledFor = scheduledFor,
        aiSummary = aiSummary,
        aiSuggestions = aiSuggestions?.let {
            // Parse JSON array string
            it.removeSurrounding("[", "]")
                .split(",")
                .map { s -> s.trim().removeSurrounding("\"") }
                .filter { s -> s.isNotEmpty() }
        } ?: emptyList(),
        audioFilePath = audioFilePath,
        createdAt = createdAt,
        updatedAt = updatedAt,
        completedAt = completedAt
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        signifier = signifier.symbol,
        status = status.value,
        roleTemplateId = roleTemplateId,
        roleTemplateName = roleTemplateName,
        projectId = projectId,
        threadId = threadId,
        tags = if (tags.isNotEmpty()) tags.joinToString(",") else null,
        migrationCount = migrationCount,
        scheduledFor = scheduledFor,
        aiSummary = aiSummary,
        aiSuggestions = if (aiSuggestions.isNotEmpty()) {
            "[${aiSuggestions.joinToString(",") { "\"$it\"" }}]"
        } else null,
        audioFilePath = audioFilePath,
        createdAt = createdAt,
        updatedAt = updatedAt,
        completedAt = completedAt
    )
}

// ==================== Project Mappers ====================

fun ProjectEntity.toDomain(): Project {
    return Project(
        id = id,
        name = name,
        description = description,
        color = color,
        icon = icon,
        status = status,
        deadline = deadline,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        name = name,
        description = description,
        color = color,
        icon = icon,
        status = status,
        deadline = deadline,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// ==================== Thread Mappers ====================

fun ThreadEntity.toDomain(): Thread {
    return Thread(
        id = id,
        name = name,
        description = description,
        noteCount = noteCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Thread.toEntity(): ThreadEntity {
    return ThreadEntity(
        id = id,
        name = name,
        description = description,
        noteCount = noteCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// ==================== Role Template Mappers ====================

fun RoleTemplateEntity.toDomain(): RoleTemplate {
    // Parse the configJson to extract prompts and settings
    // For now, return basic structure - full JSON parsing in TemplateLoader
    return RoleTemplate(
        id = id,
        name = name,
        description = description,
        category = TemplateCategory.fromValue(category),
        icon = icon,
        color = color,
        isBuiltIn = isBuiltIn,
        isActive = isActive
    )
}

fun RoleTemplate.toEntity(): RoleTemplateEntity {
    return RoleTemplateEntity(
        id = id,
        name = name,
        description = description,
        category = category.value,
        icon = icon,
        color = color,
        isBuiltIn = isBuiltIn,
        isActive = isActive,
        configJson = buildConfigJson()
    )
}

private fun RoleTemplate.buildConfigJson(): String {
    // Build JSON config string
    val prompts = capturePrompts.joinToString(",") {
        """{"field":"${it.field}","prompt":"${it.prompt}","required":${it.required}}"""
    }
    return """{"capturePrompts":[$prompts]}"""
}

// ==================== Reminder Mappers ====================

fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        noteId = noteId,
        projectId = projectId,
        title = title,
        message = message,
        triggerType = ReminderTriggerType.fromValue(triggerType),
        triggerTime = triggerTime,
        isRecurring = isRecurring,
        status = ReminderStatus.fromValue(status),
        createdAt = createdAt
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        noteId = noteId,
        projectId = projectId,
        title = title,
        message = message,
        triggerType = triggerType.value,
        triggerTime = triggerTime,
        isRecurring = isRecurring,
        status = status.value,
        createdAt = createdAt
    )
}

// ==================== Audit Log Mappers ====================

fun AuditLogEntity.toDomain(): AuditLogEntry {
    return AuditLogEntry(
        id = id,
        entityType = entityType,
        entityId = entityId,
        actionType = AuditActionType.fromValue(actionType),
        actionDetails = actionDetails,
        previousValue = previousValue,
        newValue = newValue,
        aiModel = aiModel,
        userAccepted = userAccepted,
        timestamp = timestamp
    )
}

fun AuditLogEntry.toEntity(): AuditLogEntity {
    return AuditLogEntity(
        id = id,
        entityType = entityType,
        entityId = entityId,
        actionType = actionType.value,
        actionDetails = actionDetails,
        previousValue = previousValue,
        newValue = newValue,
        aiModel = aiModel,
        userAccepted = userAccepted,
        timestamp = timestamp
    )
}
