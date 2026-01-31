package com.rolenoteai.app.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rolenoteai.app.core.database.EncryptedDatabaseFactory
import com.rolenoteai.app.core.security.AuthenticationManager
import com.rolenoteai.app.core.validation.InputValidator
import com.rolenoteai.app.data.local.RoleNoteDatabase
import com.rolenoteai.app.data.local.dao.*
import com.rolenoteai.app.data.repository.NoteRepository
import com.rolenoteai.app.data.repository.TemplateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RoleNote AI - Hilt Dependency Injection Module
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Provides application-wide dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ==================== Security ====================

    @Provides
    @Singleton
    fun provideInputValidator(): InputValidator {
        return InputValidator()
    }

    @Provides
    @Singleton
    fun provideAuthenticationManager(
        @ApplicationContext context: Context
    ): AuthenticationManager {
        return AuthenticationManager(context)
    }

    // ==================== Database ====================

    @Provides
    @Singleton
    fun provideEncryptedDatabaseFactory(
        @ApplicationContext context: Context
    ): EncryptedDatabaseFactory {
        return EncryptedDatabaseFactory(context)
    }

    @Provides
    @Singleton
    fun provideRoleNoteDatabase(
        factory: EncryptedDatabaseFactory
    ): RoleNoteDatabase {
        return factory.buildDatabase(RoleNoteDatabase::class.java, RoleNoteDatabase.DATABASE_NAME)
    }

    // ==================== DAOs ====================

    @Provides
    @Singleton
    fun provideNoteDao(database: RoleNoteDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideProjectDao(database: RoleNoteDatabase): ProjectDao {
        return database.projectDao()
    }

    @Provides
    @Singleton
    fun provideThreadDao(database: RoleNoteDatabase): ThreadDao {
        return database.threadDao()
    }

    @Provides
    @Singleton
    fun provideRoleTemplateDao(database: RoleNoteDatabase): RoleTemplateDao {
        return database.roleTemplateDao()
    }

    @Provides
    @Singleton
    fun provideAuditLogDao(database: RoleNoteDatabase): AuditLogDao {
        return database.auditLogDao()
    }

    @Provides
    @Singleton
    fun provideReminderDao(database: RoleNoteDatabase): ReminderDao {
        return database.reminderDao()
    }

    // ==================== JSON ====================

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    // ==================== Repositories ====================

    @Provides
    @Singleton
    fun provideNoteRepository(
        noteDao: NoteDao,
        auditLogDao: AuditLogDao,
        inputValidator: InputValidator
    ): NoteRepository {
        return NoteRepository(noteDao, auditLogDao, inputValidator)
    }

    @Provides
    @Singleton
    fun provideTemplateRepository(
        @ApplicationContext context: Context,
        roleTemplateDao: RoleTemplateDao,
        gson: Gson
    ): TemplateRepository {
        return TemplateRepository(context, roleTemplateDao, gson)
    }
}
