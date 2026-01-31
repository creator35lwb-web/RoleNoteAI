package com.rolenoteai.app.core.validation

import javax.inject.Inject
import javax.inject.Singleton

/**
 * RoleNote AI - Input Validation Layer
 * CTO: RNA (Claude Code Opus 4.5)
 *
 * Implements security specs from: docs/security/INPUT_VALIDATION.md
 * Trinity Validation ID: fa3e7b66
 *
 * Purpose:
 * - Sanitize all user input before AI processing
 * - Prevent prompt injection attacks
 * - Validate template data
 * - Ensure data integrity
 */
@Singleton
class InputValidator @Inject constructor() {

    companion object {
        // Maximum lengths
        const val MAX_NOTE_LENGTH = 50_000
        const val MAX_TITLE_LENGTH = 200
        const val MAX_TAG_LENGTH = 50
        const val MAX_TAGS_COUNT = 20

        // Patterns for validation
        private val DANGEROUS_PATTERNS = listOf(
            // Prompt injection patterns
            Regex("""(?i)ignore\s+(previous|above|all)\s+instructions?"""),
            Regex("""(?i)disregard\s+(previous|above|all)\s+instructions?"""),
            Regex("""(?i)forget\s+(previous|above|all)\s+instructions?"""),
            Regex("""(?i)new\s+instructions?:"""),
            Regex("""(?i)system\s*:\s*"""),
            Regex("""(?i)assistant\s*:\s*"""),
            Regex("""(?i)\[INST\]"""),
            Regex("""(?i)<\|im_start\|>"""),
            Regex("""(?i)<\|im_end\|>"""),

            // Code injection patterns
            Regex("""<script[^>]*>""", RegexOption.IGNORE_CASE),
            Regex("""javascript:""", RegexOption.IGNORE_CASE),
            Regex("""on\w+\s*=""", RegexOption.IGNORE_CASE),

            // SQL injection patterns (for extra safety)
            Regex(""";\s*DROP\s+TABLE""", RegexOption.IGNORE_CASE),
            Regex(""";\s*DELETE\s+FROM""", RegexOption.IGNORE_CASE),
            Regex("""UNION\s+SELECT""", RegexOption.IGNORE_CASE),
        )

        // Allowed signifiers (BuJo-inspired)
        private val VALID_SIGNIFIERS = setOf("•", "○", "—", "!", "?", "*", "×", ">", "<", "~")
    }

    /**
     * Validation result with detailed error information
     */
    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val reason: String, val field: String? = null) : ValidationResult()
    }

    /**
     * Sanitized input wrapper
     */
    data class SanitizedInput(
        val content: String,
        val wasModified: Boolean,
        val modifications: List<String> = emptyList()
    )

    // ==================== Note Validation ====================

    /**
     * Validate note content before storage/AI processing
     */
    fun validateNoteContent(content: String): ValidationResult {
        if (content.isBlank()) {
            return ValidationResult.Invalid("Note content cannot be empty", "content")
        }

        if (content.length > MAX_NOTE_LENGTH) {
            return ValidationResult.Invalid(
                "Note exceeds maximum length of $MAX_NOTE_LENGTH characters",
                "content"
            )
        }

        // Check for dangerous patterns
        for (pattern in DANGEROUS_PATTERNS) {
            if (pattern.containsMatchIn(content)) {
                return ValidationResult.Invalid(
                    "Content contains potentially harmful patterns",
                    "content"
                )
            }
        }

        return ValidationResult.Valid
    }

    /**
     * Sanitize note content - remove/escape dangerous content
     */
    fun sanitizeNoteContent(content: String): SanitizedInput {
        var sanitized = content
        val modifications = mutableListOf<String>()

        // Trim and normalize whitespace
        sanitized = sanitized.trim()
        if (sanitized != content.trim()) {
            modifications.add("Trimmed whitespace")
        }

        // Truncate if too long
        if (sanitized.length > MAX_NOTE_LENGTH) {
            sanitized = sanitized.take(MAX_NOTE_LENGTH)
            modifications.add("Truncated to max length")
        }

        // Remove null bytes
        if (sanitized.contains('\u0000')) {
            sanitized = sanitized.replace("\u0000", "")
            modifications.add("Removed null bytes")
        }

        // Escape dangerous patterns by adding zero-width space
        for (pattern in DANGEROUS_PATTERNS) {
            if (pattern.containsMatchIn(sanitized)) {
                sanitized = pattern.replace(sanitized) { match ->
                    // Insert zero-width space to break the pattern
                    match.value.mapIndexed { index, c ->
                        if (index == 0) "$c\u200B" else c.toString()
                    }.joinToString("")
                }
                modifications.add("Escaped potential injection pattern")
            }
        }

        return SanitizedInput(
            content = sanitized,
            wasModified = modifications.isNotEmpty(),
            modifications = modifications
        )
    }

    // ==================== Title Validation ====================

    /**
     * Validate note/project title
     */
    fun validateTitle(title: String): ValidationResult {
        if (title.isBlank()) {
            return ValidationResult.Invalid("Title cannot be empty", "title")
        }

        if (title.length > MAX_TITLE_LENGTH) {
            return ValidationResult.Invalid(
                "Title exceeds maximum length of $MAX_TITLE_LENGTH characters",
                "title"
            )
        }

        // No newlines in titles
        if (title.contains('\n') || title.contains('\r')) {
            return ValidationResult.Invalid("Title cannot contain line breaks", "title")
        }

        return ValidationResult.Valid
    }

    /**
     * Sanitize title
     */
    fun sanitizeTitle(title: String): SanitizedInput {
        var sanitized = title.trim()
            .replace(Regex("[\n\r]"), " ")
            .replace(Regex("\\s+"), " ")

        val wasModified = sanitized != title

        if (sanitized.length > MAX_TITLE_LENGTH) {
            sanitized = sanitized.take(MAX_TITLE_LENGTH)
        }

        return SanitizedInput(
            content = sanitized,
            wasModified = wasModified
        )
    }

    // ==================== Signifier Validation ====================

    /**
     * Validate signifier (BuJo bullet type)
     */
    fun validateSignifier(signifier: String): ValidationResult {
        if (signifier !in VALID_SIGNIFIERS) {
            return ValidationResult.Invalid(
                "Invalid signifier. Valid options: ${VALID_SIGNIFIERS.joinToString()}",
                "signifier"
            )
        }
        return ValidationResult.Valid
    }

    /**
     * Extract signifier from note content
     */
    fun extractSignifier(content: String): String? {
        val firstChar = content.trim().firstOrNull()?.toString()
        return if (firstChar in VALID_SIGNIFIERS) firstChar else null
    }

    // ==================== Tag Validation ====================

    /**
     * Validate a single tag
     */
    fun validateTag(tag: String): ValidationResult {
        if (tag.isBlank()) {
            return ValidationResult.Invalid("Tag cannot be empty", "tag")
        }

        if (tag.length > MAX_TAG_LENGTH) {
            return ValidationResult.Invalid(
                "Tag exceeds maximum length of $MAX_TAG_LENGTH characters",
                "tag"
            )
        }

        // Tags should be alphanumeric with hyphens/underscores
        if (!tag.matches(Regex("^[a-zA-Z0-9_-]+$"))) {
            return ValidationResult.Invalid(
                "Tags can only contain letters, numbers, hyphens, and underscores",
                "tag"
            )
        }

        return ValidationResult.Valid
    }

    /**
     * Validate list of tags
     */
    fun validateTags(tags: List<String>): ValidationResult {
        if (tags.size > MAX_TAGS_COUNT) {
            return ValidationResult.Invalid(
                "Maximum $MAX_TAGS_COUNT tags allowed",
                "tags"
            )
        }

        for (tag in tags) {
            val result = validateTag(tag)
            if (result is ValidationResult.Invalid) {
                return result
            }
        }

        return ValidationResult.Valid
    }

    // ==================== Template Validation ====================

    /**
     * Validate template JSON structure
     */
    fun validateTemplateJson(json: String): ValidationResult {
        // Basic structure validation
        if (!json.trim().startsWith("{") || !json.trim().endsWith("}")) {
            return ValidationResult.Invalid("Invalid JSON structure", "template")
        }

        // Check for required fields
        val requiredFields = listOf("id", "name", "version")
        for (field in requiredFields) {
            if (!json.contains("\"$field\"")) {
                return ValidationResult.Invalid("Missing required field: $field", "template")
            }
        }

        return ValidationResult.Valid
    }

    // ==================== AI Input Preparation ====================

    /**
     * Prepare content for AI processing
     * This is the final gate before sending to LLM
     */
    fun prepareForAI(content: String, roleContext: String? = null): SanitizedInput {
        // First sanitize
        val sanitized = sanitizeNoteContent(content)

        // Add role context prefix if provided
        val withContext = if (roleContext != null) {
            "[Role: $roleContext]\n${sanitized.content}"
        } else {
            sanitized.content
        }

        return sanitized.copy(content = withContext)
    }

    /**
     * Validate that content is safe for embedding generation
     */
    fun validateForEmbedding(content: String): ValidationResult {
        if (content.isBlank()) {
            return ValidationResult.Invalid("Content cannot be empty for embedding", "content")
        }

        // Embeddings have no dangerous patterns - just length check
        if (content.length > MAX_NOTE_LENGTH) {
            return ValidationResult.Invalid("Content too long for embedding", "content")
        }

        return ValidationResult.Valid
    }
}
