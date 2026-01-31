package com.rolenoteai.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rolenoteai.app.data.repository.TemplateRepository
import com.rolenoteai.app.domain.model.RoleTemplate
import com.rolenoteai.app.domain.model.TemplateCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * RoleNote AI - Template ViewModel
 * CTO: RNA (Claude Code Opus 4.5)
 * Phase 3b: Core Engine
 *
 * Manages role template selection and configuration
 */
@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val templateRepository: TemplateRepository
) : ViewModel() {

    // ==================== State ====================

    private val _uiState = MutableStateFlow(TemplateUiState())
    val uiState: StateFlow<TemplateUiState> = _uiState.asStateFlow()

    // ==================== Flows ====================

    val allTemplates: StateFlow<List<RoleTemplate>> = templateRepository.getAllTemplates()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeTemplate: StateFlow<RoleTemplate?> = templateRepository.getActiveTemplate()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val functionalTemplates: StateFlow<List<RoleTemplate>> =
        templateRepository.getTemplatesByCategory(TemplateCategory.FUNCTIONAL)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val cSuiteTemplates: StateFlow<List<RoleTemplate>> =
        templateRepository.getTemplatesByCategory(TemplateCategory.C_SUITE)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val customTemplates: StateFlow<List<RoleTemplate>> =
        templateRepository.getTemplatesByCategory(TemplateCategory.CUSTOM)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ==================== Initialization ====================

    init {
        // Initialize built-in templates on first launch
        viewModelScope.launch {
            templateRepository.initializeBuiltInTemplates()
        }
    }

    // ==================== Actions ====================

    /**
     * Set active template for note capture
     */
    fun setActiveTemplate(templateId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            templateRepository.setActiveTemplate(templateId).fold(
                onSuccess = { template ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            lastSelectedTemplate = template
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    /**
     * Get full template configuration (including prompts)
     */
    fun loadFullTemplateConfig(templateId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val config = templateRepository.getFullTemplateConfig(templateId)
            if (config != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentTemplateConfig = config
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load template configuration"
                    )
                }
            }
        }
    }

    /**
     * Save a custom template
     */
    fun saveCustomTemplate(template: RoleTemplate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            templateRepository.saveCustomTemplate(template).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    /**
     * Delete a custom template
     */
    fun deleteCustomTemplate(templateId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            templateRepository.deleteCustomTemplate(templateId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    /**
     * Get template by ID
     */
    suspend fun getTemplateById(id: String): RoleTemplate? {
        return templateRepository.getTemplateById(id)
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State for template screen
 */
data class TemplateUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastSelectedTemplate: RoleTemplate? = null,
    val currentTemplateConfig: TemplateRepository.FullTemplateConfig? = null
)
