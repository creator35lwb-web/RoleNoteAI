package com.rolenoteai.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rolenoteai.app.data.repository.NoteRepository
import com.rolenoteai.app.domain.model.Note
import com.rolenoteai.app.domain.model.NoteStatus
import com.rolenoteai.app.domain.model.Signifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * RoleNote AI - Note ViewModel
 * CTO: RNA (Claude Code Opus 4.5)
 * Phase 3b: Core Engine
 *
 * Manages note state and operations for UI
 */
@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    // ==================== State ====================

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote.asStateFlow()

    // ==================== Flows ====================

    val allNotes: StateFlow<List<Note>> = noteRepository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val openTasks: StateFlow<List<Note>> = noteRepository.getNotesByStatus(NoteStatus.OPEN)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val staleTasks: StateFlow<List<Note>> = noteRepository.getStaleTasks(3)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ==================== Actions ====================

    /**
     * Create a new note from raw input (parses signifier)
     */
    fun createNote(
        rawInput: String,
        roleTemplateId: String? = null,
        roleTemplateName: String? = null,
        projectId: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            noteRepository.createNoteFromInput(
                rawInput = rawInput,
                roleTemplateId = roleTemplateId,
                roleTemplateName = roleTemplateName,
                projectId = projectId
            ).fold(
                onSuccess = { note ->
                    _uiState.update { it.copy(isLoading = false, lastCreatedNote = note) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    /**
     * Create note with explicit signifier
     */
    fun createNoteWithSignifier(
        content: String,
        signifier: Signifier,
        title: String? = null,
        roleTemplateId: String? = null,
        roleTemplateName: String? = null,
        projectId: String? = null,
        tags: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            noteRepository.createNote(
                content = content,
                signifier = signifier,
                title = title,
                roleTemplateId = roleTemplateId,
                roleTemplateName = roleTemplateName,
                projectId = projectId,
                tags = tags
            ).fold(
                onSuccess = { note ->
                    _uiState.update { it.copy(isLoading = false, lastCreatedNote = note) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    /**
     * Update an existing note
     */
    fun updateNote(note: Note) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            noteRepository.updateNote(note).fold(
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
     * Mark note as complete
     */
    fun completeNote(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            noteRepository.completeNote(noteId).fold(
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
     * Migrate note (BuJo-style forward migration)
     */
    fun migrateNote(noteId: String, newDate: Long? = null, reason: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            noteRepository.migrateNote(noteId, newDate, reason).fold(
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
     * Cancel note
     */
    fun cancelNote(noteId: String, reason: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            noteRepository.cancelNote(noteId, reason).fold(
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
     * Delete note
     */
    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            noteRepository.deleteNote(noteId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    if (_selectedNote.value?.id == noteId) {
                        _selectedNote.value = null
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    /**
     * Select a note for viewing/editing
     */
    fun selectNote(noteId: String) {
        viewModelScope.launch {
            val note = noteRepository.getNoteById(noteId)
            _selectedNote.value = note
        }
    }

    /**
     * Clear selected note
     */
    fun clearSelection() {
        _selectedNote.value = null
    }

    /**
     * Search notes
     */
    fun searchNotes(query: String): Flow<List<Note>> {
        return noteRepository.searchNotes(query)
    }

    /**
     * Get notes by signifier
     */
    fun getNotesBySignifier(signifier: Signifier): Flow<List<Note>> {
        return noteRepository.getNotesBySignifier(signifier)
    }

    /**
     * Get notes by project
     */
    fun getNotesByProject(projectId: String): Flow<List<Note>> {
        return noteRepository.getNotesByProject(projectId)
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State for notes screen
 */
data class NoteUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastCreatedNote: Note? = null
)
