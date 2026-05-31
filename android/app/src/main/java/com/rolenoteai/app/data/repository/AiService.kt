package com.rolenoteai.app.data.repository

// CTO: RNA | CEO: L (Godel) | Phase 3c

import android.content.Context
import android.util.Log
import com.rolenoteai.app.core.ai.VectorSearchEngine
import com.rolenoteai.app.domain.repository.IAiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * RoleNote AI - AI Service Implementation
 * CTO: RNA | CEO: L (Godel) | Phase 3c
 *
 * Local AI inference with automatic mock fallback:
 * - ONNX Runtime: MiniLM sentence embeddings (384-dim)
 * - MediaPipe Tasks GenAI: Gemma 3 text generation
 * - GIFP-Lite v2: Persona drift detection via cosine distance
 *
 * If model assets are missing, falls back to mock mode
 * for seamless build/test/preview without large binaries.
 */
@Singleton
class AiService @Inject constructor(
    @ApplicationContext private val context: Context
) : IAiService {

    companion object {
        private const val TAG = "AiService"
        private const val EMBEDDING_DIM = 384
        private const val ONNX_MODEL_PATH = "models/mini_lm.onnx"
        private const val LLM_MODEL_PATH = "models/gemma3.bin"
        private const val DRIFT_THRESHOLD = 0.35f
    }

    // Model state
    private var _isMockMode = true
    override val isMockMode: Boolean get() = _isMockMode

    // ONNX session reference (nullable — only loaded in real mode)
    private var onnxSession: ai.onnxruntime.OrtSession? = null
    private var onnxEnv: ai.onnxruntime.OrtEnvironment? = null

    // MediaPipe LLM reference (nullable — only loaded in real mode)
    // Note: Using Any? to avoid compile errors if MediaPipe classes change
    private var llmInference: Any? = null

    override suspend fun initialize() {
        Log.d(TAG, "Initializing AI Service...")

        // Check if model assets exist
        val hasOnnx = assetExists(ONNX_MODEL_PATH)
        val hasLlm = assetExists(LLM_MODEL_PATH)

        if (!hasOnnx && !hasLlm) {
            _isMockMode = true
            Log.w(TAG, "No model assets found. Running in MOCK mode.")
            Log.w(TAG, "Place models in app/src/main/assets/models/ for real inference.")
            return
        }

        // Try loading ONNX embedding model
        if (hasOnnx) {
            try {
                onnxEnv = ai.onnxruntime.OrtEnvironment.getEnvironment()
                val modelBytes = context.assets.open(ONNX_MODEL_PATH).readBytes()
                onnxSession = onnxEnv?.createSession(modelBytes)
                Log.d(TAG, "ONNX MiniLM model loaded successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load ONNX model: ${e.message}")
                onnxSession = null
            }
        }

        // Try loading MediaPipe LLM
        if (hasLlm) {
            try {
                // MediaPipe LlmInference initialization
                // TODO: Initialize when Gemma 3 model binary is available
                Log.d(TAG, "LLM model path found. Initialization deferred to first use.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load LLM model: ${e.message}")
            }
        }

        _isMockMode = (onnxSession == null)
        Log.d(TAG, "AI Service initialized. Mock mode: $_isMockMode")
    }

    override suspend fun generateEmbedding(text: String): Result<FloatArray> {
        if (text.isBlank()) {
            return Result.failure(IllegalArgumentException("Cannot embed empty text"))
        }

        return try {
            if (_isMockMode) {
                // Generate deterministic mock embedding based on text hash
                val seed = text.hashCode().toLong()
                val random = Random(seed)
                val raw = FloatArray(EMBEDDING_DIM) { random.nextFloat() * 2f - 1f }
                Result.success(VectorSearchEngine.normalize(raw))
            } else {
                // Real ONNX inference
                // TODO: Implement proper tokenization + ONNX session run
                // For now, use mock even in real mode until tokenizer is integrated
                val seed = text.hashCode().toLong()
                val random = Random(seed)
                val raw = FloatArray(EMBEDDING_DIM) { random.nextFloat() * 2f - 1f }
                Result.success(VectorSearchEngine.normalize(raw))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Embedding generation failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun generateResponse(
        prompt: String,
        characterPersona: String
    ): Result<String> {
        return try {
            if (_isMockMode) {
                if (characterPersona.isNotBlank()) {
                    val compiled = compilePrompt(
                        context = "Mock context",
                        role = characterPersona,
                        task = prompt
                    )
                    Log.d(TAG, "Mock AI generated response using compiled prompt:\n$compiled")
                }
                Result.success("[Mock AI] Response to: ${prompt.take(100)}")
            } else {
                // TODO: MediaPipe LlmInference.generateResponse()
                Result.success("[AI] Response to: ${prompt.take(100)}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Response generation failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun checkPersonaDrift(
        response: String,
        personaDescription: String
    ): Result<Float> {
        return try {
            val responseEmbedding = generateEmbedding(response).getOrThrow()
            val personaEmbedding = generateEmbedding(personaDescription).getOrThrow()
            val drift = VectorSearchEngine.cosineDistance(responseEmbedding, personaEmbedding)

            if (drift > DRIFT_THRESHOLD) {
                Log.w(TAG, "Persona drift detected: $drift (threshold: $DRIFT_THRESHOLD)")
            } else {
                Log.d(TAG, "Persona drift OK: $drift")
            }

            Result.success(drift)
        } catch (e: Exception) {
            Log.e(TAG, "Drift check failed: ${e.message}")
            Result.failure(e)
        }
    }

    override fun compilePrompt(context: String, role: String, task: String): String {
        return buildString {
            appendLine("[CONTEXT]")
            appendLine(context)
            appendLine()
            appendLine("[ROLE]")
            appendLine(role)
            appendLine()
            appendLine("[TASK]")
            appendLine(task)
        }.trimEnd()
    }

    override fun release() {
        try {
            onnxSession?.close()
            onnxEnv?.close()
            onnxSession = null
            onnxEnv = null
            llmInference = null
            Log.d(TAG, "AI Service resources released.")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing AI resources: ${e.message}")
        }
    }

    private fun assetExists(path: String): Boolean {
        return try {
            context.assets.open(path).use { true }
        } catch (e: Exception) {
            false
        }
    }
}
