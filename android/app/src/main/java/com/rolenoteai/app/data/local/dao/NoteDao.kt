package com.rolenoteai.app.data.local.dao

import androidx.room.*
import com.rolenoteai.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * RoleNote AI - Note DAO
 * CTO: RNA (Claude Code Opus 4.5)
 */
@Dao
interface NoteDao {

    // ==================== Notes ====================

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE status = :status ORDER BY createdAt DESC")
    fun getNotesByStatus(status: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE signifier = :signifier ORDER BY createdAt DESC")
    fun getNotesBySignifier(signifier: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getNotesByProject(projectId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE threadId = :threadId ORDER BY createdAt ASC")
    fun getNotesByThread(threadId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE roleTemplateId = :templateId ORDER BY createdAt DESC")
    fun getNotesByTemplate(templateId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?

    @Query("""
        SELECT * FROM notes
        WHERE status = 'open'
        AND signifier IN ('•', '!')
        AND createdAt < :staleCutoff
        ORDER BY createdAt ASC
    """)
    fun getStaleTasks(staleCutoff: Long): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE scheduledFor IS NOT NULL
        AND scheduledFor >= :startTime
        AND scheduledFor <= :endTime
        ORDER BY scheduledFor ASC
    """)
    fun getScheduledNotes(startTime: Long, endTime: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE DATE(createdAt/1000, 'unixepoch') = DATE(:date/1000, 'unixepoch') ORDER BY createdAt DESC")
    fun getNotesForDate(date: Long): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE content LIKE '%' || :query || '%'
        OR title LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateNoteStatus(id: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET status = 'done', completedAt = :completedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markNoteComplete(id: String, completedAt: Long = System.currentTimeMillis(), updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET embedding = :embedding, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateNoteEmbedding(id: String, embedding: String, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: String)

    // ==================== Migrations ====================

    @Query("SELECT * FROM migrations WHERE noteId = :noteId ORDER BY migrationDate DESC")
    fun getMigrationsForNote(noteId: String): Flow<List<MigrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMigration(migration: MigrationEntity)

    // ==================== Statistics ====================

    @Query("SELECT COUNT(*) FROM notes WHERE status = 'done' AND completedAt >= :since")
    suspend fun getCompletedCount(since: Long): Int

    @Query("SELECT COUNT(*) FROM notes WHERE status = 'cancelled' AND updatedAt >= :since")
    suspend fun getCancelledCount(since: Long): Int

    @Query("SELECT AVG(migrationCount) FROM notes WHERE status = 'done' AND completedAt >= :since")
    suspend fun getAverageMigrationCount(since: Long): Float?

    @Query("SELECT signifier, COUNT(*) as count FROM notes GROUP BY signifier")
    suspend fun getSignifierStats(): List<SignifierStat>
}

data class SignifierStat(
    val signifier: String,
    val count: Int
)

/**
 * RoleNote AI - Project DAO
 */
@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects WHERE status = 'active' ORDER BY updatedAt DESC")
    fun getActiveProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

/**
 * RoleNote AI - Thread DAO
 */
@Dao
interface ThreadDao {

    @Query("SELECT * FROM threads ORDER BY updatedAt DESC")
    fun getAllThreads(): Flow<List<ThreadEntity>>

    @Query("SELECT * FROM threads WHERE id = :id")
    suspend fun getThreadById(id: String): ThreadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: ThreadEntity)

    @Update
    suspend fun updateThread(thread: ThreadEntity)

    @Query("UPDATE threads SET noteCount = noteCount + 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun incrementNoteCount(id: String, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteThread(thread: ThreadEntity)
}

/**
 * RoleNote AI - Role Template DAO
 */
@Dao
interface RoleTemplateDao {

    @Query("SELECT * FROM role_templates ORDER BY category, name")
    fun getAllTemplates(): Flow<List<RoleTemplateEntity>>

    @Query("SELECT * FROM role_templates WHERE category = :category ORDER BY name")
    fun getTemplatesByCategory(category: String): Flow<List<RoleTemplateEntity>>

    @Query("SELECT * FROM role_templates WHERE isActive = 1 LIMIT 1")
    fun getActiveTemplate(): Flow<RoleTemplateEntity?>

    @Query("SELECT * FROM role_templates WHERE id = :id")
    suspend fun getTemplateById(id: String): RoleTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: RoleTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<RoleTemplateEntity>)

    @Update
    suspend fun updateTemplate(template: RoleTemplateEntity)

    @Query("UPDATE role_templates SET isActive = 0")
    suspend fun deactivateAllTemplates()

    @Query("UPDATE role_templates SET isActive = 1 WHERE id = :id")
    suspend fun activateTemplate(id: String)

    @Delete
    suspend fun deleteTemplate(template: RoleTemplateEntity)

    @Query("SELECT COUNT(*) FROM role_templates")
    suspend fun getTemplateCount(): Int
}

/**
 * RoleNote AI - Audit Log DAO
 */
@Dao
interface AuditLogDao {

    @Query("SELECT * FROM audit_log ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_log WHERE entityType = :entityType AND entityId = :entityId ORDER BY timestamp DESC")
    fun getLogsForEntity(entityType: String, entityId: String): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_log WHERE actionType LIKE 'ai_%' ORDER BY timestamp DESC LIMIT :limit")
    fun getAIActionLogs(limit: Int = 100): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)

    @Query("DELETE FROM audit_log WHERE timestamp < :before")
    suspend fun deleteOldLogs(before: Long)
}

/**
 * RoleNote AI - Reminder DAO
 */
@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE status = 'pending' ORDER BY triggerTime ASC")
    fun getPendingReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE triggerTime <= :time AND status = 'pending'")
    suspend fun getDueReminders(time: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE noteId = :noteId")
    fun getRemindersForNote(noteId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET status = :status WHERE id = :id")
    suspend fun updateReminderStatus(id: String, status: String)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)
}
