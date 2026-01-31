package com.rolenoteai.app.data.repository

import com.rolenoteai.app.core.validation.InputValidator
import com.rolenoteai.app.data.local.dao.AuditLogDao
import com.rolenoteai.app.data.local.dao.NoteDao
import com.rolenoteai.app.data.local.entity.AuditLogEntity
import com.rolenoteai.app.data.local.entity.MigrationEntity
import com.rolenoteai.app.data.mapper.toDomain
import com.rolenoteai.app.data.mapper.toEntity
import com.rolenoteai.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RoleNote AI - Note Repository
 * CTO: RNA (Claude Code Opus 4.5)
 * Phase 3b: Core Engine
 *
 * Handles all note CRUD operations with:
 * - Input validation (prompt injection defense)
 * - Audit logging (Z-Guardian requirement)
 * - Signifier parsing
 */
@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val auditLogDao: AuditLogDao,
    private val inputValidator: InputValidator
) {

    // ==================== Read Operations ====================

    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getNotesByStatus(status: NoteStatus): Flow<List<Note>> {
        return noteDao.getNotesByStatus(status.value).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getNotesBySignifier(signifier: Signifier): Flow<List<Note>> {
        return noteDao.getNotesBySignifier(signifier.symbol).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getNotesByProject(projectId: String): Flow<List<Note>> {
        return noteDao.getNotesByProject(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getNotesByThread(threadId: String): Flow<List<Note>> {
        return noteDao.getNotesByThread(threadId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getNotesForDate(date: Long): Flow<List<Note>> {
        return noteDao.getNotesForDate(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getStaleTasks(thresholdDays: Int = 3): Flow<List<Note>> {
        val cutoff = System.currentTimeMillis() - (thresholdDays * 24 * 60 * 60 * 1000L)
        return noteDao.getStaleTasks(cutoff).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun searchNotes(query: String): Flow<List<Note>> {
        // Validate search query
        val sanitized = inputValidator.sanitizeNoteContent(query)
        return noteDao.searchNotes(sanitized.content).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getNoteById(id: String): Note? {
        return noteDao.getNoteById(id)?.toDomain()
    }

    // ==================== Write Operations ====================

    /**
     * Create a new note with validation and audit logging
     */
    suspend fun createNote(
        content: String,
        signifier: Signifier = Signifier.NOTE,
        title: String? = null,
        roleTemplateId: String? = null,
        roleTemplateName: String? = null,
        projectId: String? = null,
        tags: List<String> = emptyList()
    ): Result<Note> {
        // Validate content
        val contentValidation = inputValidator.validateNoteContent(content)
        if (contentValidation is InputValidator.ValidationResult.Invalid) {
            return Result.failure(IllegalArgumentException(contentValidation.reason))
        }

        // Validate title if provided
        if (title != null) {
            val titleValidation = inputValidator.validateTitle(title)
            if (titleValidation is InputValidator.ValidationResult.Invalid) {
                return Result.failure(IllegalArgumentException(titleValidation.reason))
            }
        }

        // Validate tags
        if (tags.isNotEmpty()) {
            val tagsValidation = inputValidator.validateTags(tags)
            if (tagsValidation is InputValidator.ValidationResult.Invalid) {
                return Result.failure(IllegalArgumentException(tagsValidation.reason))
            }
        }

        // Sanitize content
        val sanitized = inputValidator.sanitizeNoteContent(content)

        // Create note
        val note = Note(
            content = sanitized.content,
            signifier = signifier,
            title = title?.let { inputValidator.sanitizeTitle(it).content },
            roleTemplateId = roleTemplateId,
            roleTemplateName = roleTemplateName,
            projectId = projectId,
            tags = tags
        )

        // Save to database
        noteDao.insertNote(note.toEntity())

        // Audit log
        logAuditAction(
            entityType = "note",
            entityId = note.id,
            actionType = AuditActionType.CREATE,
            newValue = note.content
        )

        return Result.success(note)
    }

    /**
     * Create note from raw input (parses signifier from content)
     */
    suspend fun createNoteFromInput(
        rawInput: String,
        roleTemplateId: String? = null,
        roleTemplateName: String? = null,
        projectId: String? = null
    ): Result<Note> {
        val parsed = parseNoteInput(rawInput)
        return createNote(
            content = parsed.content,
            signifier = parsed.signifier,
            roleTemplateId = roleTemplateId,
            roleTemplateName = roleTemplateName,
            projectId = projectId
        )
    }

    /**
     * Update an existing note
     */
    suspend fun updateNote(note: Note): Result<Note> {
        val existing = noteDao.getNoteById(note.id)
            ?: return Result.failure(IllegalArgumentException("Note not found"))

        // Validate content
        val contentValidation = inputValidator.validateNoteContent(note.content)
        if (contentValidation is InputValidator.ValidationResult.Invalid) {
            return Result.failure(IllegalArgumentException(contentValidation.reason))
        }

        // Sanitize and update
        val sanitized = inputValidator.sanitizeNoteContent(note.content)
        val updated = note.copy(
            content = sanitized.content,
            updatedAt = System.currentTimeMillis()
        )

        noteDao.updateNote(updated.toEntity())

        // Audit log
        logAuditAction(
            entityType = "note",
            entityId = note.id,
            actionType = AuditActionType.UPDATE,
            previousValue = existing.content,
            newValue = updated.content
        )

        return Result.success(updated)
    }

    /**
     * Mark note as complete
     */
    suspend fun completeNote(noteId: String): Result<Note> {
        val note = noteDao.getNoteById(noteId)
            ?: return Result.failure(IllegalArgumentException("Note not found"))

        val now = System.currentTimeMillis()
        noteDao.markNoteComplete(noteId, now, now)

        // Audit log
        logAuditAction(
            entityType = "note",
            entityId = noteId,
            actionType = AuditActionType.UPDATE,
            actionDetails = "Marked as complete"
        )

        return Result.success(note.toDomain().copy(
            status = NoteStatus.DONE,
            completedAt = now
        ))
    }

    /**
     * Migrate note (BuJo-style)
     */
    suspend fun migrateNote(
        noteId: String,
        newDate: Long? = null,
        reason: String? = null
    ): Result<Note> {
        val note = noteDao.getNoteById(noteId)
            ?: return Result.failure(IllegalArgumentException("Note not found"))

        val now = System.currentTimeMillis()
        val migration = MigrationEntity(
            noteId = noteId,
            originalDate = note.createdAt,
            migrationDate = now,
            action = if (newDate != null) "scheduled" else "migrated",
            newDate = newDate,
            reason = reason
        )

        noteDao.insertMigration(migration)

        val newStatus = if (newDate != null) NoteStatus.SCHEDULED else NoteStatus.MIGRATED
        noteDao.updateNoteStatus(noteId, newStatus.value, now)

        // Audit log
        logAuditAction(
            entityType = "note",
            entityId = noteId,
            actionType = AuditActionType.UPDATE,
            actionDetails = "Migrated: ${reason ?: "No reason provided"}"
        )

        return Result.success(note.toDomain().copy(
            status = newStatus,
            migrationCount = note.migrationCount + 1,
            scheduledFor = newDate
        ))
    }

    /**
     * Cancel note
     */
    suspend fun cancelNote(noteId: String, reason: String? = null): Result<Note> {
        val note = noteDao.getNoteById(noteId)
            ?: return Result.failure(IllegalArgumentException("Note not found"))

        val now = System.currentTimeMillis()
        noteDao.updateNoteStatus(noteId, NoteStatus.CANCELLED.value, now)

        // Audit log
        logAuditAction(
            entityType = "note",
            entityId = noteId,
            actionType = AuditActionType.UPDATE,
            actionDetails = "Cancelled: ${reason ?: "No reason provided"}"
        )

        return Result.success(note.toDomain().copy(status = NoteStatus.CANCELLED))
    }

    /**
     * Delete note
     */
    suspend fun deleteNote(noteId: String): Result<Unit> {
        val note = noteDao.getNoteById(noteId)
            ?: return Result.failure(IllegalArgumentException("Note not found"))

        noteDao.deleteNoteById(noteId)

        // Audit log
        logAuditAction(
            entityType = "note",
            entityId = noteId,
            actionType = AuditActionType.DELETE,
            previousValue = note.content
        )

        return Result.success(Unit)
    }

    // ==================== Signifier Parsing ====================

    data class ParsedNoteInput(
        val signifier: Signifier,
        val content: String
    )

    /**
     * Parse raw input to extract signifier and content
     * Examples:
     * - "• Follow up with Sarah" -> TASK, "Follow up with Sarah"
     * - "! Urgent deadline" -> PRIORITY, "Urgent deadline"
     * - "Just a note" -> NOTE, "Just a note"
     */
    fun parseNoteInput(rawInput: String): ParsedNoteInput {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) {
            return ParsedNoteInput(Signifier.NOTE, "")
        }

        val firstChar = trimmed.first().toString()
        val signifier = Signifier.fromSymbol(firstChar)

        return if (signifier != null) {
            // Remove signifier and leading whitespace
            val content = trimmed.drop(1).trimStart()
            ParsedNoteInput(signifier, content)
        } else {
            // No signifier found, treat as note
            ParsedNoteInput(Signifier.NOTE, trimmed)
        }
    }

    // ==================== Statistics ====================

    suspend fun getCompletedCount(sinceDaysAgo: Int = 7): Int {
        val since = System.currentTimeMillis() - (sinceDaysAgo * 24 * 60 * 60 * 1000L)
        return noteDao.getCompletedCount(since)
    }

    suspend fun getCancelledCount(sinceDaysAgo: Int = 7): Int {
        val since = System.currentTimeMillis() - (sinceDaysAgo * 24 * 60 * 60 * 1000L)
        return noteDao.getCancelledCount(since)
    }

    suspend fun getAverageMigrationCount(sinceDaysAgo: Int = 30): Float {
        val since = System.currentTimeMillis() - (sinceDaysAgo * 24 * 60 * 60 * 1000L)
        return noteDao.getAverageMigrationCount(since) ?: 0f
    }

    // ==================== Audit Logging ====================

    private suspend fun logAuditAction(
        entityType: String,
        entityId: String,
        actionType: AuditActionType,
        actionDetails: String? = null,
        previousValue: String? = null,
        newValue: String? = null,
        aiModel: String? = null,
        userAccepted: Boolean? = null
    ) {
        val log = AuditLogEntity(
            entityType = entityType,
            entityId = entityId,
            actionType = actionType.value,
            actionDetails = actionDetails,
            previousValue = previousValue,
            newValue = newValue,
            aiModel = aiModel,
            userAccepted = userAccepted
        )
        auditLogDao.insertLog(log)
    }
}
