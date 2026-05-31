package com.rolenoteai.app.domain.repository

import com.rolenoteai.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface INoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNotesByStatus(status: NoteStatus): Flow<List<Note>>
    fun getNotesBySignifier(signifier: Signifier): Flow<List<Note>>
    fun getNotesByProject(projectId: String): Flow<List<Note>>
    fun getNotesByThread(threadId: String): Flow<List<Note>>
    fun getNotesForDate(date: Long): Flow<List<Note>>
    fun getStaleTasks(thresholdDays: Int = 3): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun getNoteById(id: String): Note?

    suspend fun createNote(
        content: String,
        signifier: Signifier = Signifier.NOTE,
        title: String? = null,
        roleTemplateId: String? = null,
        roleTemplateName: String? = null,
        projectId: String? = null,
        tags: List<String> = emptyList()
    ): Result<Note>

    suspend fun createNoteFromInput(
        rawInput: String,
        roleTemplateId: String? = null,
        roleTemplateName: String? = null,
        projectId: String? = null
    ): Result<Note>

    suspend fun updateNote(note: Note): Result<Note>
    suspend fun completeNote(noteId: String): Result<Note>
    suspend fun migrateNote(noteId: String, newDate: Long? = null, reason: String? = null): Result<Note>
    suspend fun cancelNote(noteId: String, reason: String? = null): Result<Note>
    suspend fun deleteNote(noteId: String): Result<Unit>

    fun parseNoteInput(rawInput: String): ParsedNoteInput

    suspend fun getCompletedCount(sinceDaysAgo: Int = 7): Int
    suspend fun getCancelledCount(sinceDaysAgo: Int = 7): Int
    suspend fun getAverageMigrationCount(sinceDaysAgo: Int = 30): Float

    /** Phase 3c: Retrieve semantically similar notes using vector search with time-decay */
    suspend fun retrieveSemanticContext(query: String, limit: Int = 5): List<Note>

    data class ParsedNoteInput(
        val signifier: Signifier,
        val content: String
    )
}
