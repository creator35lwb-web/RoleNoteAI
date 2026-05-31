package com.rolenoteai.app.data.repository

import com.rolenoteai.app.core.validation.InputValidator
import com.rolenoteai.app.data.local.dao.AuditLogDao
import com.rolenoteai.app.data.local.dao.NoteDao
import com.rolenoteai.app.data.local.entity.AuditLogEntity
import com.rolenoteai.app.data.local.entity.MigrationEntity
import com.rolenoteai.app.data.mapper.toDomain
import com.rolenoteai.app.data.mapper.toEntity
import com.rolenoteai.app.domain.model.*
import com.rolenoteai.app.domain.repository.INoteRepository
import com.rolenoteai.app.domain.repository.IAiService
import com.rolenoteai.app.core.ai.VectorSearchEngine
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val inputValidator: InputValidator,
    private val aiService: IAiService
) : INoteRepository {

    // ==================== Read Operations ====================

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotesByStatus(status: NoteStatus): Flow<List<Note>> {
        return noteDao.getNotesByStatus(status.value).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotesBySignifier(signifier: Signifier): Flow<List<Note>> {
        return noteDao.getNotesBySignifier(signifier.symbol).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotesByProject(projectId: String): Flow<List<Note>> {
        return noteDao.getNotesByProject(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotesByThread(threadId: String): Flow<List<Note>> {
        return noteDao.getNotesByThread(threadId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotesForDate(date: Long): Flow<List<Note>> {
        return noteDao.getNotesForDate(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getStaleTasks(thresholdDays: Int): Flow<List<Note>> {
        val cutoff = System.currentTimeMillis() - (thresholdDays * 24 * 60 * 60 * 1000L)
        return noteDao.getStaleTasks(cutoff).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        // Validate search query
        val sanitized = inputValidator.sanitizeNoteContent(query)
        return noteDao.searchNotes(sanitized.content).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getNoteById(id: String): Note? {
        return noteDao.getNoteById(id)?.toDomain()
    }

    // ==================== Write Operations ====================

    /**
     * Create a new note with validation and audit logging
     */
    override suspend fun createNote(
        content: String,
        signifier: Signifier,
        title: String?,
        roleTemplateId: String?,
        roleTemplateName: String?,
        projectId: String?,
        tags: List<String>
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

        // Phase 3c: Generate embedding in background
        try {
            aiService.generateEmbedding(note.content).onSuccess { embedding ->
                val embeddingJson = Gson().toJson(embedding)
                noteDao.updateNoteEmbedding(note.id, embeddingJson)
            }
        } catch (e: Exception) {
            // Non-critical — note is saved even without embedding
        }

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
    override suspend fun createNoteFromInput(
        rawInput: String,
        roleTemplateId: String?,
        roleTemplateName: String?,
        projectId: String?
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
    override suspend fun updateNote(note: Note): Result<Note> {
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

        // Phase 3c: Generate/update embedding in background
        try {
            aiService.generateEmbedding(updated.content).onSuccess { embedding ->
                val embeddingJson = Gson().toJson(embedding)
                noteDao.updateNoteEmbedding(updated.id, embeddingJson)
            }
        } catch (e: Exception) {
            // Non-critical — note is saved even without embedding
        }

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
    override suspend fun completeNote(noteId: String): Result<Note> {
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
    override suspend fun migrateNote(
        noteId: String,
        newDate: Long?,
        reason: String?
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
    override suspend fun cancelNote(noteId: String, reason: String?): Result<Note> {
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
    override suspend fun deleteNote(noteId: String): Result<Unit> {
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

    // ==================== Semantic Context (Phase 3c) ====================

    /**
     * Retrieve semantically similar notes using vector search with time-decay.
     * Uses ONNX MiniLM embeddings + VectorSearchEngine cosine scoring.
     */
    override suspend fun retrieveSemanticContext(query: String, limit: Int): List<Note> {
        return try {
            // Generate query embedding
            val queryEmbedding = aiService.generateEmbedding(query).getOrElse {
                return emptyList()
            }

            // Fetch all notes with stored embeddings
            val notesWithEmbeddings = noteDao.getNotesWithEmbeddings()
            if (notesWithEmbeddings.isEmpty()) return emptyList()

            val gson = Gson()
            val floatArrayType = object : TypeToken<FloatArray>() {}.type
            val now = System.currentTimeMillis()
            val hourMs = 3_600_000L

            // Build candidates: (noteId, embedding, ageInHours)
            val candidates = notesWithEmbeddings.mapNotNull { entity ->
                val embedding: FloatArray? = try {
                    gson.fromJson(entity.embedding, floatArrayType)
                } catch (e: Exception) {
                    null
                }
                if (embedding != null) {
                    val ageHours = ((now - entity.createdAt) / hourMs).toInt()
                    Triple(entity.id, embedding, ageHours)
                } else null
            }

            // Run vector search with time-decay
            val topResults = VectorSearchEngine.topK(
                query = queryEmbedding,
                candidates = candidates,
                topK = limit
            )

            // Fetch full notes by ID
            topResults.mapNotNull { (noteId, _) ->
                noteDao.getNoteById(noteId)?.toDomain()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ==================== Signifier Parsing ====================

    /**
     * Parse raw input to extract signifier and content
     * Examples:
     * - "• Follow up with Sarah" -> TASK, "Follow up with Sarah"
     * - "! Urgent deadline" -> PRIORITY, "Urgent deadline"
     * - "Just a note" -> NOTE, "Just a note"
     */
    override fun parseNoteInput(rawInput: String): INoteRepository.ParsedNoteInput {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) {
            return INoteRepository.ParsedNoteInput(Signifier.NOTE, "")
        }

        val firstChar = trimmed.first().toString()
        val signifier = Signifier.fromSymbol(firstChar)

        return if (signifier != null) {
            // Remove signifier and leading whitespace
            val content = trimmed.drop(1).trimStart()
            INoteRepository.ParsedNoteInput(signifier, content)
        } else {
            // No signifier found, treat as note
            INoteRepository.ParsedNoteInput(Signifier.NOTE, trimmed)
        }
    }

    // ==================== Statistics ====================

    override suspend fun getCompletedCount(sinceDaysAgo: Int): Int {
        val since = System.currentTimeMillis() - (sinceDaysAgo * 24 * 60 * 60 * 1000L)
        return noteDao.getCompletedCount(since)
    }

    override suspend fun getCancelledCount(sinceDaysAgo: Int): Int {
        val since = System.currentTimeMillis() - (sinceDaysAgo * 24 * 60 * 60 * 1000L)
        return noteDao.getCancelledCount(since)
    }

    override suspend fun getAverageMigrationCount(sinceDaysAgo: Int): Float {
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
