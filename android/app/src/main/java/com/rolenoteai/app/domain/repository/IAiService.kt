package com.rolenoteai.app.domain.repository

// CTO: RNA | CEO: L (Godel) | Phase 3c

/**
 * RoleNote AI - AI Service Interface
 * CTO: RNA | CEO: L (Godel) | Phase 3c
 *
 * Domain contract for local AI operations:
 * - Embedding generation (ONNX MiniLM)
 * - LLM text generation (Gemma 3 via MediaPipe)
 * - Persona drift detection (GIFP-Lite v2)
 * - MACP prompt compilation
 */
interface IAiService {

    /** Generate a semantic embedding vector for the given text */
    suspend fun generateEmbedding(text: String): Result<FloatArray>

    /** Generate a text response using local LLM with persona context */
    suspend fun generateResponse(prompt: String, characterPersona: String = ""): Result<String>

    /** Check cosine drift between a response and its expected persona. Returns drift score 0..1 */
    suspend fun checkPersonaDrift(response: String, personaDescription: String): Result<Float>

    /** Compile a structured MACP prompt envelope from context, role, and task */
    fun compilePrompt(context: String, role: String, task: String): String

    /** Whether the service is running in mock/fallback mode (no model assets present) */
    val isMockMode: Boolean

    /** Initialize the AI service (load models). Call once at app startup. */
    suspend fun initialize()

    /** Release model resources */
    fun release()
}
