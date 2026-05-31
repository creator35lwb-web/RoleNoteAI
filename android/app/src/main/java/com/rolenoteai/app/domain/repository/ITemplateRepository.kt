package com.rolenoteai.app.domain.repository

import com.rolenoteai.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ITemplateRepository {
    fun getAllTemplates(): Flow<List<RoleTemplate>>
    fun getTemplatesByCategory(category: TemplateCategory): Flow<List<RoleTemplate>>
    fun getActiveTemplate(): Flow<RoleTemplate?>
    suspend fun getTemplateById(id: String): RoleTemplate?

    suspend fun setActiveTemplate(templateId: String): Result<RoleTemplate>
    suspend fun saveCustomTemplate(template: RoleTemplate): Result<RoleTemplate>
    suspend fun deleteCustomTemplate(templateId: String): Result<Unit>

    suspend fun initializeBuiltInTemplates()
    suspend fun getFullTemplateConfig(templateId: String): FullTemplateConfig?

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
