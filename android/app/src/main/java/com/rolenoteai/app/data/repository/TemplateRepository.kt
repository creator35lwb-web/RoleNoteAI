package com.rolenoteai.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rolenoteai.app.data.local.dao.RoleTemplateDao
import com.rolenoteai.app.data.local.entity.RoleTemplateEntity
import com.rolenoteai.app.data.mapper.toDomain
import com.rolenoteai.app.domain.model.*
import com.rolenoteai.app.domain.repository.ITemplateRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RoleNote AI - Template Repository
 * CTO: RNA (Claude Code Opus 4.5)
 * Phase 3b: Core Engine
 *
 * Manages role templates:
 * - Loads 16 built-in templates from assets
 * - Handles custom templates
 * - Manages active template selection
 */
@Singleton
class TemplateRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val templateDao: RoleTemplateDao,
    private val gson: Gson
) : ITemplateRepository {

    // ==================== Read Operations ====================

    override fun getAllTemplates(): Flow<List<RoleTemplate>> {
        return templateDao.getAllTemplates().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTemplatesByCategory(category: TemplateCategory): Flow<List<RoleTemplate>> {
        return templateDao.getTemplatesByCategory(category.value).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveTemplate(): Flow<RoleTemplate?> {
        return templateDao.getActiveTemplate().map { it?.toDomain() }
    }

    override suspend fun getTemplateById(id: String): RoleTemplate? {
        return templateDao.getTemplateById(id)?.toDomain()
    }

    // ==================== Write Operations ====================

    /**
     * Set the active template
     */
    override suspend fun setActiveTemplate(templateId: String): Result<RoleTemplate> {
        val template = templateDao.getTemplateById(templateId)
            ?: return Result.failure(IllegalArgumentException("Template not found"))

        templateDao.deactivateAllTemplates()
        templateDao.activateTemplate(templateId)

        return Result.success(template.toDomain().copy(isActive = true))
    }

    /**
     * Save a custom template
     */
    override suspend fun saveCustomTemplate(template: RoleTemplate): Result<RoleTemplate> {
        val entity = RoleTemplateEntity(
            id = template.id,
            name = template.name,
            description = template.description,
            category = TemplateCategory.CUSTOM.value,
            icon = template.icon,
            color = template.color,
            isBuiltIn = false,
            isActive = false,
            configJson = gson.toJson(template)
        )

        templateDao.insertTemplate(entity)
        return Result.success(template)
    }

    /**
     * Delete a custom template (cannot delete built-in)
     */
    override suspend fun deleteCustomTemplate(templateId: String): Result<Unit> {
        val template = templateDao.getTemplateById(templateId)
            ?: return Result.failure(IllegalArgumentException("Template not found"))

        if (template.isBuiltIn) {
            return Result.failure(IllegalArgumentException("Cannot delete built-in template"))
        }

        templateDao.deleteTemplate(template)
        return Result.success(Unit)
    }

    // ==================== Initialization ====================

    /**
     * Load built-in templates from assets on first launch
     * Also reloads if fewer than expected templates are found
     */
    override suspend fun initializeBuiltInTemplates() = withContext(Dispatchers.IO) {
        try {
            val count = templateDao.getTemplateCount()
            val expectedCount = 19 // 11 functional + 8 c-suite
            if (count >= expectedCount) {
                return@withContext // Already initialized with all templates
            }

            // Clear any partial data and reload
            if (count > 0) {
                templateDao.deleteAllBuiltInTemplates()
            }

            val templates = mutableListOf<RoleTemplateEntity>()

        // Load functional templates
        val functionalTemplates = listOf(
            "project-manager", "developer", "accounting", "marketing",
            "human-resources", "business-administration", "technical-backend",
            "technical-frontend", "customer-services", "financial-advisor",
            "compliance-feedback"
        )

        for (templateId in functionalTemplates) {
            loadTemplateFromAssets("templates/functional/$templateId.json")?.let {
                templates.add(it)
            }
        }

        // Load c-suite templates
        val cSuiteTemplates = listOf(
            "executive", "ceo", "coo", "cto", "cfo", "cino", "cmo-monitor", "cro"
        )

        for (templateId in cSuiteTemplates) {
            loadTemplateFromAssets("templates/c-suite/$templateId.json")?.let {
                templates.add(it)
            }
        }

        // Insert all templates
        if (templates.isNotEmpty()) {
            templateDao.insertTemplates(templates)

            // Set first template as active by default
            templateDao.activateTemplate(templates.first().id)
        }
        } catch (e: Exception) {
            e.printStackTrace()
            // Continue without templates - they'll load next time
        }
    }

    /**
     * Load a template from assets folder
     */
    private fun loadTemplateFromAssets(path: String): RoleTemplateEntity? {
        return try {
            val json = context.assets.open(path).bufferedReader().use { it.readText() }
            val templateJson = gson.fromJson(json, TemplateJson::class.java)

            RoleTemplateEntity(
                id = templateJson.id,
                name = templateJson.name,
                description = templateJson.description,
                category = templateJson.category ?: "functional",
                icon = templateJson.icon ?: "note",
                color = templateJson.color ?: "#3B82F6",
                isBuiltIn = true,
                isActive = false,
                configJson = json
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getFullTemplateConfig(templateId: String): ITemplateRepository.FullTemplateConfig? {
        val entity = templateDao.getTemplateById(templateId) ?: return null

        return try {
            gson.fromJson(entity.configJson, ITemplateRepository.FullTemplateConfig::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ==================== JSON Models ====================

    private data class TemplateJson(
        val id: String,
        val name: String,
        val version: String?,
        val description: String?,
        val category: String?,
        val icon: String?,
        val color: String?
    )
}
