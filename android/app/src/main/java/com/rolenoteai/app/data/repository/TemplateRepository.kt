package com.rolenoteai.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rolenoteai.app.data.local.dao.RoleTemplateDao
import com.rolenoteai.app.data.local.entity.RoleTemplateEntity
import com.rolenoteai.app.data.mapper.toDomain
import com.rolenoteai.app.domain.model.*
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
) {

    // ==================== Read Operations ====================

    fun getAllTemplates(): Flow<List<RoleTemplate>> {
        return templateDao.getAllTemplates().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTemplatesByCategory(category: TemplateCategory): Flow<List<RoleTemplate>> {
        return templateDao.getTemplatesByCategory(category.value).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getActiveTemplate(): Flow<RoleTemplate?> {
        return templateDao.getActiveTemplate().map { it?.toDomain() }
    }

    suspend fun getTemplateById(id: String): RoleTemplate? {
        return templateDao.getTemplateById(id)?.toDomain()
    }

    // ==================== Write Operations ====================

    /**
     * Set the active template
     */
    suspend fun setActiveTemplate(templateId: String): Result<RoleTemplate> {
        val template = templateDao.getTemplateById(templateId)
            ?: return Result.failure(IllegalArgumentException("Template not found"))

        templateDao.deactivateAllTemplates()
        templateDao.activateTemplate(templateId)

        return Result.success(template.toDomain().copy(isActive = true))
    }

    /**
     * Save a custom template
     */
    suspend fun saveCustomTemplate(template: RoleTemplate): Result<RoleTemplate> {
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
    suspend fun deleteCustomTemplate(templateId: String): Result<Unit> {
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
     */
    suspend fun initializeBuiltInTemplates() = withContext(Dispatchers.IO) {
        val count = templateDao.getTemplateCount()
        if (count > 0) {
            return@withContext // Already initialized
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

    /**
     * Get full template configuration including prompts
     */
    suspend fun getFullTemplateConfig(templateId: String): FullTemplateConfig? {
        val entity = templateDao.getTemplateById(templateId) ?: return null

        return try {
            gson.fromJson(entity.configJson, FullTemplateConfig::class.java)
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

    data class FullTemplateConfig(
        val id: String,
        val name: String,
        val version: String = "1.0",
        val description: String? = null,
        val icon: String = "note",
        val color: String = "#3B82F6",
        val category: String = "functional",
        val capturePrompts: List<CapturePromptJson> = emptyList(),
        val suggestionRules: List<SuggestionRuleJson> = emptyList(),
        val execution: ExecutionSettingsJson = ExecutionSettingsJson()
    )

    data class CapturePromptJson(
        val field: String,
        val prompt: String,
        val required: Boolean = false
    )

    data class SuggestionRuleJson(
        val trigger: String,
        val action: String,
        val priority: Int = 0
    )

    data class ExecutionSettingsJson(
        val signifiers_enabled: Boolean = true,
        val default_signifier: String = "task",
        val migration_prompt_days: List<Int> = listOf(3, 7, 14),
        val weekly_review: Boolean = true,
        val monthly_review: Boolean = true,
        val auto_threading: Boolean = true,
        val stale_task_threshold_days: Int = 5
    )
}
