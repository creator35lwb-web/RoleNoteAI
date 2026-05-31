package com.rolenoteai.app.core.validation

import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
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
            Regex("""(?i)ignore\s+(?:all\s+|any\s+)?(?:previous|above|all)\s+instructions?"""),
            Regex("""(?i)disregard\s+(?:all\s+|any\s+)?(?:previous|above|all)\s+instructions?"""),
            Regex("""(?i)forget\s+(?:all\s+|any\s+)?(?:previous|above|all)\s+instructions?"""),
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
     * Validate template JSON structure and schema using Gson
     */
    fun validateTemplateJson(json: String): ValidationResult {
        if (json.isBlank()) {
            return ValidationResult.Invalid("Template JSON cannot be empty", "template")
        }

        val jsonElement = try {
            JsonParser.parseString(json)
        } catch (e: JsonSyntaxException) {
            return ValidationResult.Invalid("Invalid JSON syntax: ${e.message}", "template")
        }

        if (!jsonElement.isJsonObject) {
            return ValidationResult.Invalid("Template JSON must be a JSON object", "template")
        }

        val obj = jsonElement.asJsonObject

        // 1. Validate required fields
        val requiredFields = listOf("id", "name", "version")
        for (field in requiredFields) {
            if (!obj.has(field) || obj.get(field).isJsonNull) {
                return ValidationResult.Invalid("Missing required field: $field", field)
            }
            val element = obj.get(field)
            if (!element.isJsonPrimitive || !element.asJsonPrimitive.isString) {
                return ValidationResult.Invalid("Field $field must be a string", field)
            }
            if (element.asString.isBlank()) {
                return ValidationResult.Invalid("Field $field cannot be blank", field)
            }
        }

        // 2. Validate optional description, icon, color, category
        val stringFields = listOf("description", "icon", "color", "category")
        for (field in stringFields) {
            if (obj.has(field) && !obj.get(field).isJsonNull) {
                val element = obj.get(field)
                if (!element.isJsonPrimitive || !element.asJsonPrimitive.isString) {
                    return ValidationResult.Invalid("Field $field must be a string", field)
                }
            }
        }

        // 3. Validate capturePrompts if present
        if (obj.has("capturePrompts") && !obj.get("capturePrompts").isJsonNull) {
            val capturePromptsElement = obj.get("capturePrompts")
            if (!capturePromptsElement.isJsonArray) {
                return ValidationResult.Invalid("capturePrompts must be an array", "capturePrompts")
            }
            val arr = capturePromptsElement.asJsonArray
            for (i in 0 until arr.size()) {
                val item = arr.get(i)
                if (!item.isJsonObject) {
                    return ValidationResult.Invalid("Each item in capturePrompts must be an object", "capturePrompts[$i]")
                }
                val promptObj = item.asJsonObject
                if (!promptObj.has("field") || promptObj.get("field").isJsonNull || !promptObj.get("field").isJsonPrimitive || !promptObj.get("field").asJsonPrimitive.isString || promptObj.get("field").asString.isBlank()) {
                    return ValidationResult.Invalid("Each capturePrompt must have a non-empty string 'field'", "capturePrompts[$i].field")
                }
                if (!promptObj.has("prompt") || promptObj.get("prompt").isJsonNull || !promptObj.get("prompt").isJsonPrimitive || !promptObj.get("prompt").asJsonPrimitive.isString || promptObj.get("prompt").asString.isBlank()) {
                    return ValidationResult.Invalid("Each capturePrompt must have a non-empty string 'prompt'", "capturePrompts[$i].prompt")
                }
                if (promptObj.has("required") && !promptObj.get("required").isJsonNull) {
                    val req = promptObj.get("required")
                    if (!req.isJsonPrimitive || !req.asJsonPrimitive.isBoolean) {
                        return ValidationResult.Invalid("Field 'required' in capturePrompts must be a boolean", "capturePrompts[$i].required")
                    }
                }
            }
        }

        // 4. Validate suggestionRules if present
        if (obj.has("suggestionRules") && !obj.get("suggestionRules").isJsonNull) {
            val suggestionRulesElement = obj.get("suggestionRules")
            if (!suggestionRulesElement.isJsonArray) {
                return ValidationResult.Invalid("suggestionRules must be an array", "suggestionRules")
            }
            val arr = suggestionRulesElement.asJsonArray
            for (i in 0 until arr.size()) {
                val item = arr.get(i)
                if (!item.isJsonObject) {
                    return ValidationResult.Invalid("Each item in suggestionRules must be an object", "suggestionRules[$i]")
                }
                val ruleObj = item.asJsonObject
                if (!ruleObj.has("trigger") || ruleObj.get("trigger").isJsonNull || !ruleObj.get("trigger").isJsonPrimitive || !ruleObj.get("trigger").asJsonPrimitive.isString || ruleObj.get("trigger").asString.isBlank()) {
                    return ValidationResult.Invalid("Each suggestionRule must have a non-empty string 'trigger'", "suggestionRules[$i].trigger")
                }
                if (!ruleObj.has("action") || ruleObj.get("action").isJsonNull || !ruleObj.get("action").isJsonPrimitive || !ruleObj.get("action").asJsonPrimitive.isString || ruleObj.get("action").asString.isBlank()) {
                    return ValidationResult.Invalid("Each suggestionRule must have a non-empty string 'action'", "suggestionRules[$i].action")
                }
                if (ruleObj.has("priority") && !ruleObj.get("priority").isJsonNull) {
                    val prio = ruleObj.get("priority")
                    if (!prio.isJsonPrimitive || !prio.asJsonPrimitive.isNumber) {
                        return ValidationResult.Invalid("Field 'priority' in suggestionRules must be a number", "suggestionRules[$i].priority")
                    }
                }
            }
        }

        // 5. Validate execution settings if present
        if (obj.has("execution") && !obj.get("execution").isJsonNull) {
            val executionElement = obj.get("execution")
            if (!executionElement.isJsonObject) {
                return ValidationResult.Invalid("execution settings must be a JSON object", "execution")
            }
            val execObj = executionElement.asJsonObject
            val booleanFields = listOf("signifiers_enabled", "weekly_review", "monthly_review", "auto_threading")
            for (field in booleanFields) {
                if (execObj.has(field) && !execObj.get(field).isJsonNull) {
                    val elem = execObj.get(field)
                    if (!elem.isJsonPrimitive || !elem.asJsonPrimitive.isBoolean) {
                        return ValidationResult.Invalid("Field $field in execution must be a boolean", "execution.$field")
                    }
                }
            }
            if (execObj.has("default_signifier") && !execObj.get("default_signifier").isJsonNull) {
                val elem = execObj.get("default_signifier")
                if (!elem.isJsonPrimitive || !elem.asJsonPrimitive.isString) {
                    return ValidationResult.Invalid("Field default_signifier in execution must be a string", "execution.default_signifier")
                }
            }
            if (execObj.has("stale_task_threshold_days") && !execObj.get("stale_task_threshold_days").isJsonNull) {
                val elem = execObj.get("stale_task_threshold_days")
                if (!elem.isJsonPrimitive || !elem.asJsonPrimitive.isNumber) {
                    return ValidationResult.Invalid("Field stale_task_threshold_days in execution must be a number", "execution.stale_task_threshold_days")
                }
            }
            if (execObj.has("migration_prompt_days") && !execObj.get("migration_prompt_days").isJsonNull) {
                val elem = execObj.get("migration_prompt_days")
                if (!elem.isJsonArray) {
                    return ValidationResult.Invalid("Field migration_prompt_days in execution must be a JSON array", "execution.migration_prompt_days")
                }
                val daysArr = elem.asJsonArray
                for (j in 0 until daysArr.size()) {
                    val day = daysArr.get(j)
                    if (!day.isJsonPrimitive || !day.asJsonPrimitive.isNumber) {
                        return ValidationResult.Invalid("Each element in migration_prompt_days must be a number", "execution.migration_prompt_days[$j]")
                    }
                }
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
